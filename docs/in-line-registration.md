# In Line Registration

In-Line registration is a feature where attendees who are registering **at con**
can enter their information from their phones on the Kumoricon website,
and it will be imported in to an order when they pay at the door, 
speeding up registration.

The data is encrypted, and must be decrypted with a private key,
specified in the `inLineRegistration.privateKey` setting in the
`application.properties` file. 
This key **must** match the public key used on the Kumoricon 
website, or the data won't be decrypted.

Test data in `testdata/test_inLineRegistration.json`, and will
be imported if you copy that file to the location specified in the
`inLineRegistration.onlineInputPath` setting.

In production, a cron job will download new data on a schedule (probably 
every 30 seconds) and save that file to the correct location.

Notes on various fields:
* uuid - A unique UUID for that particular Attendee
* orderUuid - A unique UUID for that particular order (group of attendees)
* confirmationCode - A short 6 character, relatively easy to pronounce
  string given to attendees after they complete in line registration,
  which can be used to look up their information. **It is not necessarily
  unique** - multiple orders may have the same code (but generally
  won't - but that's a corner case you have to handle).
* membershipType - May be "Weekend" or "Day". Day badges are only for the
  day that someone buys a badge - Friday on Friday, Saturday on Saturday, 
  etc.


### Example payload (decrypted):

```json
{
  "attendees": [
    {
      "uuid": "9cda461d-752b-4251-842e-8ed7cb31211b",
      "firstName": "Spike",
      "lastName": "Spiegel",
      "nameOnIdSame": true,
      "firstNameOnId": "Spike",
      "lastNameOnId": "Spiegel",
      "birthDay": 1,
      "birthMonth": "1",
      "birthYear": 1997,
      "phone": "1231231231",
      "email": "spike@example.com",
      "postal": "12345",
      "country": "United States",
      "emergencyName": "Ein",
      "emergencyPhone": "3213213212",
      "parentContactSeparate": false,
      "parentName": "Ein",
      "parentPhone": "3213213212",
      "pronouns": "",
      "membershipType": "Weekend"
    },
    {
      "uuid": "f5774f94-4d5f-4146-8f0c-0d2718e7acdc",
      "firstName": "Edward",
      "lastName": "Wong Hau Pepelu Tivruski IV",
      "nameOnIdSame": false,
      "firstNameOnId": "Fran\u00e7oise",
      "lastNameOnId": "Appledelhi",
      "birthDay": 19,
      "birthMonth": "1",
      "birthYear": 2011,
      "phone": "1231231231",
      "email": "",
      "postal": "12345",
      "country": "United States",
      "emergencyName": "Ein",
      "emergencyPhone": "3213213212",
      "parentContactSeparate": true,
      "parentName": "Appledelhi Siniz Hesap L\u00fctfen",
      "parentPhone": "1112223333",
      "pronouns": "she\/her",
      "membershipType": "Weekend"
    },
    {
      "uuid": "598f350c-cf56-4cb9-9b9b-5e353c5b5283",
      "firstName": "Jet",
      "lastName": "Black",
      "nameOnIdSame": true,
      "firstNameOnId": "Jet",
      "lastNameOnId": "Black",
      "birthDay": 31,
      "birthMonth": "12",
      "birthYear": 1992,
      "phone": "5125125112",
      "email": "jet@example.com",
      "postal": "12345",
      "country": "United States",
      "emergencyName": "Ein",
      "emergencyPhone": "3213213212",
      "parentContactSeparate": true,
      "parentName": "Appledelhi Siniz Hesap L\u00fctfen",
      "parentPhone": "1112223333",
      "pronouns": "",
      "membershipType": "Day"
    }
  ],
  "orderId": "fac5739f-419b-4203-b3f8-3be5ad199dbc"
}
```