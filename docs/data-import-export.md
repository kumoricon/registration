Data Import/Export
==================

File locations are configured in the .properties file. Example
data files can be found in `/docs/testdata`.

File transfers are handled by scripts in the `registration-filetransfer`
repository, and should generally be automated with cron. Imports are desgned
to handle multiple files/changes over time.

Import paths and filenames are configured in:
```
registration.attendeeImportPath=/tmp/registration/import
registration.attendeeImportGlob=attendee*.json
registration.onlineDLQPath=/tmp/registration/importDLQ

staff.onlineImportPath=/tmp/registration/import
staff.onlineImportGlob=staff*.json
staff.onlineDLQPath=/tmp/registration/staffImportDLQ

staffbadge.badgeimagepath=/tmp/registration/badgeImage

inLineRegistration.privateKey=in-line-registration-private.pem
inLineRegistration.onlineImportPath=/tmp/registration/import
inLineRegistration.onlineImportGlob=inline*.json
inLineRegistration.onlineDLQPath=/tmp/registration/importDLQ

eventReservation.exportPath=/tmp/registration/eventReservation
```

- Imports
  - `attendee-*.json` Attendee data from website
  - `staff-*.json` Staff data from website
  - `inline-*.json` In-Line Registration from website. The data itself is encrypted
    and depends on having the appropriate .pem file configured in 
    `inLineRegistration.privateKey`.
  - Badge images: read from `staffbadge.badgeimagepath` when printing staff/guest badges
- Exports
  - `eventReservation/export-<timestamp>.json` Exports checked in members for the event reservation system
    to the path configured in `eventReservation.exportPath`
  - Staff photos: saved to the directory configured in `staffreg.file.uploaddir` and should be saved after con
