#!/usr/bin/env python3
"""
Checks that app/src/main/res/values/strings.xml and
app/src/main/res/values-en/strings.xml have identical <string name="..."> keys.
Exits with 0 if mirrored, 1 if any key is missing or extra.
"""
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def get_string_keys(file_path: Path) -> set:
    if not file_path.exists():
        sys.exit(f"Error: File not found: {file_path}")
    tree = ET.parse(file_path)
    root = tree.getroot()
    return {
        elem.attrib["name"]
        for elem in root.findall("string")
        if "name" in elem.attrib
    }


def main() -> int:
    repo_root = Path(__file__).resolve().parent.parent
    tr_path = repo_root / "app/src/main/res/values/strings.xml"
    en_path = repo_root / "app/src/main/res/values-en/strings.xml"

    tr_keys = get_string_keys(tr_path)
    en_keys = get_string_keys(en_path)

    missing_in_en = sorted(tr_keys - en_keys)
    missing_in_tr = sorted(en_keys - tr_keys)

    if not missing_in_en and not missing_in_tr:
        print(f"OK: All {len(tr_keys)} string keys match between values and values-en.")
        return 0

    print("ERROR: String mirror check failed!")
    if missing_in_en:
        print(f"  Missing in values-en/strings.xml ({len(missing_in_en)} keys):")
        for k in missing_in_en:
            print(f"    - {k}")
    if missing_in_tr:
        print(f"  Missing in values/strings.xml ({len(missing_in_tr)} keys):")
        for k in missing_in_tr:
            print(f"    - {k}")
    return 1


if __name__ == "__main__":
    sys.exit(main())
