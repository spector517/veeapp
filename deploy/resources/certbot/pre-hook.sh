#!/usr/bin/env bash
set -euo pipefail

if ufw status 2>/dev/null | grep -q "Status: active"; then
    ufw allow 80/tcp
fi
