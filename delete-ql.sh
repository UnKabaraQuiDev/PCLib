
#!/usr/bin/env bash
set -euo pipefail

# Usage:
# ./cleanup-codeql-history.sh OWNER REPO
# ./cleanup-codeql-history.sh OWNER REPO --delete
# ./cleanup-codeql-history.sh OWNER REPO --delete --include-last

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 OWNER REPO [--delete] [--include-last]" >&2
  exit 2
fi

OWNER="$1"
REPO="$2"
shift 2

DELETE=false
INCLUDE_LAST=false

for arg in "$@"; do
  case "$arg" in
    --delete) DELETE=true ;;
    --include-last) INCLUDE_LAST=true ;;
    *)
      echo "Unknown option: $arg" >&2
      exit 2
      ;;
  esac
done

command -v gh >/dev/null || {
  echo "Install GitHub CLI first: https://cli.github.com/" >&2
  exit 1
}
command -v python3 >/dev/null || {
  echo "Python 3 is required." >&2
  exit 1
}

gh auth status

API="repos/$OWNER/$REPO/code-scanning/analyses"

echo "Repository: $OWNER/$REPO"
echo "Mode: $([[ "$DELETE" == true ]] && echo DELETE || echo DRY RUN)"
echo

# Read all pages. Fail safely if any request fails.
analyses=$(gh api --paginate "$API?per_page=100" \
  --jq '.[] | {
    id: .id,
    ref: .ref,
    tool: .tool.name,
    category: .category,
    created_at: .created_at,
    deletable: .deletable
  }' | python3 -c '
import json, sys
items = []
for line in sys.stdin:
    line = line.strip()
    if line:
        items.append(json.loads(line))
print(json.dumps(items))
')


ANALYSES_JSON="$analyses" python3 - "$OWNER" "$REPO" "$DELETE" "$INCLUDE_LAST" <<'PY'
import json, os, sys, subprocess

owner, repo, delete, include_last = sys.argv[1:]
items = json.loads(os.environ["ANALYSES_JSON"])

codeql = [a for a in items if a.get("tool") == "CodeQL"]

print(f"Total analyses returned: {len(items)}")
print(f"CodeQL analyses: {len(codeql)}")
print()

for a in codeql:
    print(
        f"id={a['id']} deletable={a.get('deletable')} "
        f"date={a.get('created_at')} ref={a.get('ref')} "
        f"category={a.get('category')}"
    )

if delete != "true":
    print("\nDry run only. No analyses deleted.")
    sys.exit(0)

deleted = 0

while True:
    result = subprocess.run(
        ["gh", "api", "--paginate",
         f"repos/{owner}/{repo}/code-scanning/analyses?per_page=100"],
        capture_output=True, text=True
    )
    if result.returncode:
        print(result.stderr, file=sys.stderr)
        sys.exit("Failed to refresh analyses; stopping.")

    current = []
    decoder = json.JSONDecoder()
    s = result.stdout.strip()
    pos = 0

    while pos < len(s):
        while pos < len(s) and s[pos].isspace():
            pos += 1
        if pos >= len(s):
            break
        obj, pos = decoder.raw_decode(s, pos)
        if isinstance(obj, list):
            current.extend(obj)

    candidates = [
        a for a in current
        if a.get("tool", {}).get("name") == "CodeQL"
        and a.get("deletable") is True
    ]

    if not candidates:
        print("No more deletable CodeQL analyses.")
        break

    # Keep the final analysis for each ref/tool/category by default.
    target = None
    for candidate in candidates:
        key = (
            candidate.get("ref"),
            candidate.get("tool", {}).get("name"),
            candidate.get("category")
        )
        same_set = [
            a for a in current
            if (
                a.get("ref"),
                a.get("tool", {}).get("name"),
                a.get("category")
            ) == key
            and a.get("tool", {}).get("name") == "CodeQL"
        ]

        if include_last == "true" or len(same_set) > 1:
            target = candidate
            break

    if target is None:
        print("Only final analyses remain; stopping to preserve history.")
        break

    url = (
        f"repos/{owner}/{repo}/code-scanning/analyses/"
        f"{target['id']}"
    )
    if include_last == "true":
        url += "?confirm_delete=true"

    print(f"Deleting analysis {target['id']} ({target.get('ref')})")
    subprocess.run(["gh", "api", "--method", "DELETE", url], check=True)
    deleted += 1

print(f"Deleted {deleted} analyses.")
PY
