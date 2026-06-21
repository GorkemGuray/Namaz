# Sitemap Redesign & Modernization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Consolidate multiple sitemap files into a single, unified `sitemap.xml` with multilingual alternates (`xhtml:link`), moving the city registry to a clean JSON format.

**Architecture:** We will use `website/cities.json` as the single source of truth for the list of Turkish and English cities. We will update `generate_static.py` to parse this JSON file and dynamically build the unified sitemap.xml.

**Tech Stack:** Python 3 (xml.etree.ElementTree), JSON, robots.txt

## Global Constraints
- Target URL path: `https://gorkemguray.github.io/Namaz-App/`
- Sitemap path: `website/sitemap.xml`
- Output formatting: Indented XML with encoding declaration (`<?xml version="1.0" encoding="UTF-8"?>`)

---

### Task 1: Migrate City List to JSON Source of Truth
**Files:**
- Create: `website/cities.json`
- Test: Verify the file exists and has correct count of entries.

- [ ] **Step 1: Verify the extracted cities.json file is correctly formatted**
Verify that the `website/cities.json` file has 976 entries in `tr` and 983 entries in `en`.
Run: `python3 -c "import json; d=json.load(open('website/cities.json')); print(len(d['tr']), len(d['en']))"`
Expected: `976 983`

- [ ] **Step 2: Commit cities.json**
```bash
git add website/cities.json
git commit -m "data: migrate city lists to centralized cities.json"
```

---

### Task 2: Update generate_static.py for JSON and Unified Sitemap Generation
**Files:**
- Modify: `website/generate_static.py`
- Test: Verify generator compiles correctly and generates `website/sitemap.xml`.

- [ ] **Step 1: Modify imports and main() method to read cities.json**
Change `load_cities` to read from the JSON file instead of parsing sitemap-tr.xml and sitemap-en.xml.
```python
def load_cities(lang):
    """Load cities from cities.json for a given language."""
    import json
    json_path = os.path.join(WEBSITE_DIR, 'cities.json')
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return [(c['param'], c['priority']) for c in data[lang]]
```
And update `main()` to call this new function:
```python
def main():
    tr_cities = load_cities('tr')
    en_cities = load_cities('en')
    ...
```

- [ ] **Step 2: Add generate_sitemap_xml() function in generate_static.py**
Add a function to construct and write the unified, schema-compliant `sitemap.xml` with alternate links.
```python
def generate_sitemap_xml(tr_cities, en_cities):
    """Generate a single, unified, fully compliant multilingual sitemap.xml."""
    import xml.etree.ElementTree as ET
    from datetime import date
    
    ET.register_namespace('', 'http://www.sitemaps.org/schemas/sitemap/0.9')
    ET.register_namespace('xhtml', 'http://www.w3.org/1999/xhtml')
    ET.register_namespace('xsi', 'http://www.w3.org/2001/XMLSchema-instance')
    
    root = ET.Element('urlset')
    root.set('xmlns', 'http://www.sitemaps.org/schemas/sitemap/0.9')
    root.set('xmlns:xhtml', 'http://www.w3.org/1999/xhtml')
    root.set('xmlns:xsi', 'http://www.w3.org/2001/XMLSchema-instance')
    root.set('xsi:schemaLocation', 'http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/sitemap.xsd')
    
    today = date.today().isoformat()
    
    # Root Homepages
    for base_url, lang in [(BASE_URL_TR, 'tr'), (BASE_URL_EN, 'en')]:
        url_node = ET.SubElement(root, 'url')
        loc_node = ET.SubElement(url_node, 'loc')
        loc_node.text = f"{base_url}/"
        
        # TR alt
        link_tr = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_tr.set('rel', 'alternate')
        link_tr.set('hreflang', 'tr')
        link_tr.set('href', f"{BASE_URL_TR}/")
        
        # EN alt
        link_en = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_en.set('rel', 'alternate')
        link_en.set('hreflang', 'en')
        link_en.set('href', f"{BASE_URL_EN}/")
        
        # Default alt
        link_def = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_def.set('rel', 'alternate')
        link_def.set('hreflang', 'x-default')
        link_def.set('href', f"{BASE_URL_TR}/")
        
        lastmod = ET.SubElement(url_node, 'lastmod')
        lastmod.text = today
        changefreq = ET.SubElement(url_node, 'changefreq')
        changefreq.text = 'daily'
        priority = ET.SubElement(url_node, 'priority')
        priority.text = '1.0'
        
    tr_params = {param for param, _ in tr_cities}
    en_params = {param for param, _ in en_cities}
    
    # Turkish pages (sehir)
    for param, p in tr_cities:
        url_node = ET.SubElement(root, 'url')
        loc_node = ET.SubElement(url_node, 'loc')
        loc_node.text = f"{BASE_URL_TR}/sehir/{param}/"
        
        link_tr = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_tr.set('rel', 'alternate')
        link_tr.set('hreflang', 'tr')
        link_tr.set('href', f"{BASE_URL_TR}/sehir/{param}/")
        
        if param in en_params:
            link_en = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
            link_en.set('rel', 'alternate')
            link_en.set('hreflang', 'en')
            link_en.set('href', f"{BASE_URL_EN}/city/{param}/")
            
        link_def = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_def.set('rel', 'alternate')
        link_def.set('hreflang', 'x-default')
        link_def.set('href', f"{BASE_URL_TR}/sehir/{param}/")
        
        lastmod = ET.SubElement(url_node, 'lastmod')
        lastmod.text = today
        changefreq = ET.SubElement(url_node, 'changefreq')
        changefreq.text = 'daily'
        priority = ET.SubElement(url_node, 'priority')
        priority.text = p
        
    # English pages (city)
    for param, p in en_cities:
        url_node = ET.SubElement(root, 'url')
        loc_node = ET.SubElement(url_node, 'loc')
        loc_node.text = f"{BASE_URL_EN}/city/{param}/"
        
        if param in tr_params:
            link_tr = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
            link_tr.set('rel', 'alternate')
            link_tr.set('hreflang', 'tr')
            link_tr.set('href', f"{BASE_URL_TR}/sehir/{param}/")
            
        link_en = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_en.set('rel', 'alternate')
        link_en.set('hreflang', 'en')
        link_en.set('href', f"{BASE_URL_EN}/city/{param}/")
        
        link_def = ET.SubElement(url_node, '{http://www.w3.org/1999/xhtml}link')
        link_def.set('rel', 'alternate')
        link_def.set('hreflang', 'x-default')
        link_def.set('href', f"{BASE_URL_TR}/sehir/{param}/" if param in tr_params else f"{BASE_URL_EN}/city/{param}/")
        
        lastmod = ET.SubElement(url_node, 'lastmod')
        lastmod.text = today
        changefreq = ET.SubElement(url_node, 'changefreq')
        changefreq.text = 'daily'
        priority = ET.SubElement(url_node, 'priority')
        priority.text = p
        
    tree = ET.ElementTree(root)
    ET.indent(tree, space="  ", level=0)
    sitemap_path = os.path.join(WEBSITE_DIR, 'sitemap.xml')
    tree.write(sitemap_path, encoding='utf-8', xml_declaration=True)
    print(f"  Created sitemap.xml at {sitemap_path}")
```
Call this function inside the `main()` method of `generate_static.py`:
```python
    # Generate unified sitemap
    print("Generating sitemap.xml...")
    generate_sitemap_xml(tr_cities, en_cities)
```

- [ ] **Step 3: Test generation locally**
Run: `python3 website/generate_static.py`
Expected: Outputs completion messages including "Created sitemap.xml" and successfully regenerates all city HTML pages.

- [ ] **Step 4: Commit changes to generate_static.py**
```bash
git add website/generate_static.py
git commit -m "feat: generate unified multilingual sitemap.xml from cities.json"
```

---

### Task 3: Update robots.txt to Reference the New Sitemap
**Files:**
- Modify: `website/robots.txt`
- Test: Verify the sitemap URL points to `sitemap.xml` instead of `sitemap_index.xml`.

- [ ] **Step 1: Edit robots.txt**
Change `Sitemap: https://gorkemguray.github.io/Namaz-App/sitemap_index.xml` to `Sitemap: https://gorkemguray.github.io/Namaz-App/sitemap.xml`.

- [ ] **Step 2: Commit robots.txt**
```bash
git add website/robots.txt
git commit -m "seo: update robots.txt to point to new unified sitemap"
```

---

### Task 4: Clean up Obsolete Sitemap Files
**Files:**
- Delete: `website/sitemap_index.xml`
- Delete: `website/sitemap-tr.xml`
- Delete: `website/sitemap-en.xml`

- [ ] **Step 1: Delete obsolete XML files**
Run: `rm website/sitemap_index.xml website/sitemap-tr.xml website/sitemap-en.xml`

- [ ] **Step 2: Commit deletions**
```bash
git rm website/sitemap_index.xml website/sitemap-tr.xml website/sitemap-en.xml
git commit -m "cleanup: remove obsolete separate sitemaps"
```
