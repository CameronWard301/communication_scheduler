# Generate Https Keys
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout ./private.key -out public.crt

running locally: go to browser and type https://localhost:3000
When the browser displays unsafe warning, click anywhere on the page and type `thisisunsafe` to bypass the warning.
The web portal will then be able to connect to the server.
