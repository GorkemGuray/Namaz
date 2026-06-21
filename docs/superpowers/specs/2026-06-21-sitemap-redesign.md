# Sitemap Redesign & Modernization Specification

This document details the redesign of the Namaz Vakitleri website's sitemap generation to resolve indexing issues in Google Search Console (GSC) and ensure compliance with modern SEO standards.

## 1. Problem Statement

Google Search Console (GSC) currently rejects the sitemap for `https://gorkemguray.github.io/Namaz-App/`. The key reasons identified are:
- **Index Parsing Overhead:** GSC has to parse `sitemap_index.xml` and then fetch/parse two additional XML files (`sitemap-tr.xml` and `sitemap-en.xml`), which frequently fails or times out when hosted on subfolder-based GitHub Pages.
- **Lack of Multilingual Alternate Mapping:** The site serves both Turkish (`/sehir/`) and English (`/en/city/`) versions of each city page, but the sitemap does not declare these alternates via the `xhtml:link` namespace.
- **Redundant/Fragile XML Structure:** The list of cities is hardcoded inside large, static XML files, making updates and validation prone to syntax errors.

---

## 2. Proposed Solution & Architecture

We will implement a unified, dynamically-generated, and fully compliant multilingual sitemap system.

### A. Consolidate into a Single `sitemap.xml`
Instead of using a sitemap index and multiple sub-sitemaps, we will consolidate all URLs into a single, unified `sitemap.xml` file at the root. For ~2000 URLs, a single file is much more reliable and avoids GSC index parsing failures.

### B. Implement Multilingual alternate links (`xhtml:link`)
We will declare the XHTML namespace:
`xmlns:xhtml="http://www.w3.org/1999/xhtml"`

For every city URL that exists in both languages, we will cross-reference both versions. For example:
```xml
  <url>
    <loc>https://gorkemguray.github.io/Namaz-App/sehir/abana/</loc>
    <xhtml:link rel="alternate" hreflang="tr" href="https://gorkemguray.github.io/Namaz-App/sehir/abana/" />
    <xhtml:link rel="alternate" hreflang="en" href="https://gorkemguray.github.io/Namaz-App/en/city/abana/" />
    <xhtml:link rel="alternate" hreflang="x-default" href="https://gorkemguray.github.io/Namaz-App/sehir/abana/" />
    <lastmod>2026-06-21</lastmod>
    <changefreq>daily</changefreq>
    <priority>0.6</priority>
  </url>
```
This ensures Google indexes the pages correctly and serves the appropriate language version depending on the user's location and settings.

### C. Standardize the Schema Locations
We will add standard schema validation attributes to the XML root:
```xml
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
        xmlns:xhtml="http://www.w3.org/1999/xhtml"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9
                            http://www.sitemaps.org/schemas/sitemap/sitemap.xsd">
```

### D. Centralize City Registry in `cities.json`
We will replace the checked-in XML sitemaps as a data source with `website/cities.json`.
- `cities.json` contains two arrays: `tr` and `en` representing the URL slug parameter names for each city.

### E. Automate Generation via `generate_static.py`
We will update `generate_static.py` to:
1. Load city registry from `cities.json`.
2. Generate a single, compliant `sitemap.xml` with:
   - Root homepages (`/` and `/en/`).
   - All Turkish cities with their English alternate links.
   - All English cities with their Turkish alternate links.
   - International English-only cities (without Turkish alternates).
3. Generate the static HTML city index pages and individual city folders.

### F. Update Configuration Files
- Update `robots.txt` to point to `https://gorkemguray.github.io/Namaz-App/sitemap.xml`.
- Remove `sitemap_index.xml`, `sitemap-tr.xml`, and `sitemap-en.xml`.

---

## 3. Verification Plan

1. **Local Script Validation:** Parse and validate the newly generated `sitemap.xml` using a Python script to ensure it is 100% syntactically correct and references the right namespaces.
2. **Hreflang Validation:** Confirm that every Turkish page in the sitemap has a corresponding alternate tag referencing the English version and vice-versa.
3. **Static Generation Run:** Run `python3 website/generate_static.py` and verify all city HTML folders are correctly populated and contain identical canonical/alternate links.
