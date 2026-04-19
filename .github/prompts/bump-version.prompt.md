---
description: "Bump the project version across all files that contain it"
agent: "agent"
argument-hint: "<new version, e.g. 0.2.0>"
---

# Bump Version

Update the version number in all locations where it is hardcoded. The argument is the new version string.

## Files to update

1. `backend/pom.xml` — `<version>` tag
2. `frontend/package.json` — `"version"` field
3. `sdk/package.json` — `"version"` field
4. `deploy/deb/DEBIAN/control` — `Version:` field

After updating, verify all four files contain the new version.
