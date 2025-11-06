# Contributing to OriGo-EventorApi

Thank you for your interest in contributing to OriGo-EventorApi! We welcome contributions from the community.

## Conventional Commits

This project follows the [Conventional Commits](https://www.conventionalcommits.org/) specification for commit messages and pull request titles.

### Pull Request Title Format

**All pull request titles MUST follow the Conventional Commits format:**

```
<type>: <description>
```

or with optional scope:

```
<type>(<scope>): <description>
```

### Allowed Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Changes that do not affect the meaning of the code (white-space, formatting, etc)
- **refactor**: A code change that neither fixes a bug nor adds a feature
- **perf**: A code change that improves performance
- **test**: Adding missing tests or correcting existing tests
- **build**: Changes that affect the build system or external dependencies
- **ci**: Changes to CI configuration files and scripts
- **chore**: Other changes that don't modify src or test files
- **revert**: Reverts a previous commit

### Examples of Valid PR Titles

✅ Good examples:
- `feat: add support for relay events`
- `fix: correct time calculation in results`
- `docs: update API documentation`
- `chore(deps): update spring boot to 3.5.7`
- `refactor: simplify event converter logic`

❌ Bad examples:
- `Add new feature` (missing type)
- `Feature: Add support` (wrong type, should be 'feat')
- `fix: Fix bug` (subject should start with lowercase: 'fix: fix bug')
- `feat:add feature` (missing space after colon)

### Why Conventional Commits?

This project uses [release-please](https://github.com/googleapis/release-please) to automatically:
- Generate changelogs
- Determine semantic version bumps
- Create releases

Conventional commits ensure that these automated processes work correctly.

## Contribution Workflow

1. **Fork the repository**
   ```bash
   # Click the "Fork" button on GitHub
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/OriGo-EventorApi.git
   cd OriGo-EventorApi
   ```

3. **Create a feature branch**
   ```bash
   git checkout -b feature-branch-name
   ```

4. **Make your changes**
   - Follow the existing code style
   - Add tests for new functionality
   - Update documentation as needed

5. **Test your changes**
   ```bash
   mvn clean install
   mvn test
   ```

6. **Commit your changes**
   ```bash
   git commit -m "feat: add new feature"
   ```
   
   Note: Individual commits don't need to follow conventional commits, but it's good practice. The PR title is what matters most.

7. **Push to your fork**
   ```bash
   git push origin feature-branch-name
   ```

8. **Create a Pull Request**
   - Go to the original repository on GitHub
   - Click "New Pull Request"
   - Select your branch
   - **Important**: Ensure your PR title follows the Conventional Commits format
   - Provide a clear description of your changes

## Code Review Process

- A maintainer will review your PR
- Address any feedback or requested changes
- Once approved, your PR will be merged (typically using squash merge)
- The PR title will become the commit message, so ensure it's descriptive and follows the format

## Questions?

If you have questions about contributing, feel free to open an issue for discussion.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
