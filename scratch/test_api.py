import urllib.request
import urllib.parse
import json

def test_search(query):
    encoded_query = urllib.parse.quote(query)
    url = f"https://ezanvakti.imsakiyem.com/api/locations/search/districts?q={encoded_query}&countryId=2"
    req = urllib.request.Request(
        url, 
        headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}
    )
    try:
        with urllib.request.urlopen(req) as response:
            res_bytes = response.read()
            data = json.loads(res_bytes.decode('utf-8'))
            if data.get('success'):
                districts = data.get('data', [])
                print(f"Query: '{query}' -> Success: True, Count: {len(districts)}")
                for d in districts[:5]:
                    print(f"  - ID: {d.get('_id')}, Name: {d.get('name')}, NameEn: {d.get('name_en')}")
            else:
                print(f"Query: '{query}' -> Success: False, Response: {data}")
    except Exception as e:
        print(f"Error for query '{query}': {e}")

print("Testing other districts:")
test_search("üsküdar")
test_search("uskudar")
test_search("ÜSKÜDAR")
test_search("PENDİK")
test_search("pendik")
test_search("PENDIK")
test_search("BEŞİKTAŞ")
test_search("besiktas")
test_search("maltepe")
test_search("MALTEPE")
