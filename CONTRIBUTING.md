# Contributing to PCLib

Please read this guide before opening an issue or pull request.

---

# How to Contribute

You can contribute in several ways:

* Report bugs
* Suggest new utilities or improvements
* Improve documentation
* Add tests
* Submit code fixes or features

All contributions are welcome.

---

# Reporting Bugs

Before opening a bug report:

1. Check if the issue already exists.
2. Use the latest version of the library.
3. Provide enough detail to reproduce the problem, preferably an example in an independent repository.

Include:

* Java version
* Operating system
* Code example
* Expected behavior
* Actual behavior
* Stack trace if available

Create a [GitHub Issue](https://github.com/UnKabaraQuiDev/PCLib/issues)

---

# Suggesting Features

When proposing a new feature:

* Explain the problem it solves
* Provide a simple API example
* Keep the scope focused

PCLib aims to stay lightweight and divided into small submodules.

---

# Suggesting Enhancements

When proposing an enhancement for an exising feature:

* Explain the problem it solves
* Explain what changed, how and why

PCLib aims to stay lightweight and divided into small submodules.

---

# Development Setup

1. Fork the repository
2. Clone your fork
3. Make sure your fork's `dev` branch is up to date
4. Create a new branch from `dev`:

   ```text
   <sub-project>/feature/<name|issue-id>
   ```

   or:

   ```text
   <sub-project>/issue/<issue-id>
   ```

   or:

   ```test
   <sub-project>/enhance/<issue-id>
   ```

   For example:

   ```text
   pclib-db/feature/1234
   ```
5. Make your changes
6. Open a [Pull Request](https://github.com/UnKabaraQuiDev/PCLib/pulls) targeting `dev`

---

# Branching Strategy

`main` contains the latest released version of PCLib. The `dev` branch contains the changes planned for the next release.

> [!NOTE]
> Contributions should always be based on `dev`, not `main`.

The branch flow is:

```text
main
    ↑
    │ rebased & merged for each version
    │
dev
    ↑
    │ sqashed & merged for each feature or issue
    │
<sub-project>/feature/...
```

Feature, issue and enhancement branches should be created from `dev` and pull requests should target `dev`.
When a version is ready, `dev` is merged into `main` and the version is released.

Do not create feature branches from `main` unless specifically requested.

---

# Code Style

Follow these guidelines:

1. Use clear and simple Java code
2. Follow standard Java naming conventions
3. Keep methods focused and small
4. Avoid unnecessary dependencies
5. Write minimal doc

Eclipse format file [eclipse-format.xml](https://github.com/UnKabaraQuiDev/PCLib/blob/main/eclipse-format.xml).

Spotless is also configured. Use:

```bash
mvn initialize spotless:apply
```

> [!TIP]
> Enable local git hooks using `./.githooks/enable`. This will automatically apply formatting and check for code-style violations before every commit.

---

# Tests

All new features and bug fixes should include tests.

Guidelines:

1. Use JUnit (`/src/test/java/`)
2. Cover normal use cases
3. Cover edge cases when possible
4. Add non-regression tests for bugs that were re-introduced

---

# Pull Request Guidelines

Preferably open an issue and a PR draft before starting.

When opening a pull request:

1. Make sure the code builds
2. Run all tests
3. Keep PRs focused on one feature or bug fix
4. Write a clear description
5. Target the `dev` branch

Include:

1. The issue
2. Your solution
3. Any important information, such as breaking changes

---

# Code of Conduct

Be respectful and constructive.

The goal is to build a useful library together :3
