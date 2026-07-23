# Contributing to PCLib

Please read this guide before opening an issue or pull request.

---

# How to Contribute

You can contribute in several ways:

- Report bugs
- Suggest new utilities or improvements
- Improve documentation
- Add tests
- Submit code fixes or features

All contributions are welcome.

---

# Reporting Bugs

Before opening a bug report:

1. Check if the issue already exists.
2. Use the latest version of the library.
3. Provide enough detail to reproduce the problem, preferably an example in an independent repository.

Include:

- Java version
- Operating system
- Code example
- Expected behavior
- Actual behavior
- Stack trace if available

Create a [GitHub Issue](https://github.com/UnKabaraQuiDev/PCLib/issues)

---

# Suggesting Features

When proposing a new feature:

- Explain the problem it solves
- Provide a simple API example
- Keep the scope small and focused
- Avoid adding heavy dependencies

PCLib aims to stay lightweight and divided into small submodules.

---

# Development Setup

1. Fork the repository
2. Clone your fork
3. Create a new branch `git checkout -b sub-project/feature/my-feature`, named: `sub-project/feature/<name|issue id>`, `sub-project/issue/<issue id>` (example: `pclib-db/feature/1234`)
4. Open a [Pull Request](https://github.com/UnKabaraQuiDev/PCLib/pulls)
5. Make your changes

Enable local git hooks using `./.githooks/enable`. There is a pre-commit hook for formatting

---

# Code Style

Follow these guidelines:

1. Use clear and simple Java code
2. Follow standard Java naming conventions
3. Keep methods focused and small
4. Avoid unnecessary dependencies
5. Write minimal doc

Eclipse format file [eclipse-format.xml](https://github.com/UnKabaraQuiDev/PCLib/blob/main/eclipse-format.xml).
Spotless is also configured, use `mvn initialize spotless:apply` to apply. Enable the git hooks using `./.githooks/enable` to do this automatically before every commit.

---

# Tests

All new features and bug fixes should include tests.

Guidelines:

1. Use junit (`/src/test/java/`)
2. Cover normal use cases
3. Cover edge cases when possible

---

# Pull Request Guidelines

Preferably open (an) issue(s) and PR Draft before starting.

When opening a pull request:

1. Make sure the code builds
2. Run all tests
3. Keep PRs focused on one feature/bug fix
4. Write a clear description

Include:

1. The issue
2. Your solutions
3. Any important information, like breaking changes


---

# Code of Conduct

Be respectful and constructive.
The goal is to build a useful library together :3
