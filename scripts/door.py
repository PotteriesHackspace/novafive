import requests
import time
url="http://air.potterieshackspace.org:3333/"


r0 = requests.get(url)
r=r0.text
for x in r.split("\n"):
    if "logged into" in x:
        stripped = x.strip("\r\n").strip("<br />").strip("</html>")
        ripped = stripped.replace("logged into Hackspace", "").replace("font color=\"blue\">", "").strip()
        print(ripped)
