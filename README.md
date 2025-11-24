# Korisnici

**OWNER**  
email: owner@gmail.com  
password: 123456789

**ADMIN**  
email: admin@gmail.com  
password: 123456789

**USER**  
email: user@gmail.com  
password: 123456789

---

# BASE URL
localhost:8765

---

# URL-ovi

## Users Service

- `GET: localhost:8770/users` — vraca sve korisnike — to pise da ne moze niko od korisnika i zasticeno je, jedini nacin je ovako da se pristupi ukoliko zatreba za provere nekih funckionalnosti  
- `GET: localhost:8770/users/email?email=user@gmail.com` — ista prica i ovde

- `POST: localhost:8765/users/newAdmin` — kreira admina  
- `POST: localhost:8765/users/newOwner` — kreira owner-a  
- `POST: localhost:8765/users/newUser` — kreira usera  

**primer tela zahteva za kreiranje:**
```json
{
  "email": "user@gmail.com",
  "password": "123456789",
  "role": "user"
}
```

- `PUT: localhost:8765/users/updateUser` — admin moze da azurira samo USERE dok owner moze sve  
**primer tela zahteva:**
```json
{
  "email": "user@gmail.com",
  "password": "123456789",
  "role": "user"
}
```

- `DELETE: localhost:8765/users/removeUser?email=user1@gmail.com` — brisanje koriisnika ima pravo samo owner

---

## Currency-Exchange
- `GET: localhost:8765/currency-exchange?from=EUR&to=chf` — mogu sve uloge

---

## Currency-Conversion
- `GET: localhost:8765/currency-conversion?from=eur&to=rsd&quantity=10` — samo user moze

---

## Bank-Account

- `GET: localhost:8765/bank-accounts` — moze samo admin da pregleda sve racune  
- `GET: localhost:8765/bank-accounts/email` — samo user, vraca njegov racun

- `POST: localhost:8765/bank-accounts/create` — samo admin, kreiranje racuna  
**primer tela zahteva:**
```json
{
  "email": "user@gmail.com",
  "rsdAmount": 0.00,
  "eurAmount": 0.00,
  "usdAmount": 0.00,
  "chfAmount": 0.00,
  "gbpAmount": 0.00
}
```

- `PUT: localhost:8765/bank-accounts/update` — samo admin, azuriranje bankovnog racuna  
**primer tela zahteva:**
```json
{
  "email": "user@gmail.com",
  "rsdAmount": 10.00,
  "eurAmount": 100.00,
  "usdAmount": 0.00,
  "chfAmount": 100.00,
  "gbpAmount": 0.00
}
```

- `DELETE: localhost:8200/bank-accounts/remove?email=user@gmail.com` — ovo je u slucaju da zatreba, moze mu se samo ovako pristupiti. jer u specifikaciji se ne pominje da je bilo kojoj ulozi dozvoljeno brisanje

---

## Crypto-exchange
- `GET: localhost:8765/crypto-exchange?from=btc&to=ETH` — svi korisnici mogu da vide kurs razmene kripto valuta

---

## Crypto-conversion
- `GET: localhost:8765/crypto-conversion?from=btc&to=ltc&quantity=10` — mogu samo user-i

---

## Crypto-wallet

- `GET: localhost:8765/crypto-wallets` — vraca sve novcanike, moze samo admin  
- `GET: localhost:8765/crypto-wallets/email` — vraca novcanik user-u, samo user moze da pristupi

- `POST: localhost:8765/crypto-wallets/create` — kreiranje novcanika, moze samo admin  
**primer tela zahteva:**
```json
{
  "email": "user@gmail.com",
  "btcAmount": 0.00,
  "ethAmount": 0.00,
  "ltcAmount": 0.00
}
```

- `PUT: localhost:8765/crypto-wallets/update` — azuriranje novcanika, moze samo admin  
**primer tela zahteva:**
```json
{
  "email": "user@gmail.com",
  "btcAmount": 11.00,
  "ethAmount": 1.00,
  "ltcAmount": 1.00
}
```

- `DELETE: localhost:8300/crypto-wallets/remove?email=user1@gmail.com` — ovo je u slucaju da zatreba, moze mu se samo ovako pristupiti. jer u specifikaciji se ne pominje da je bilo kojoj ulozi dozvoljeno brisanje

---

## Trade-service
- `GET: localhost:8765/trade-service?from=eur&to=btc&quantity=1` — mogu mu pristupiti samo korisnici sa ulogom user
