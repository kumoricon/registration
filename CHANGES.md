# Change History

Version 0.7.1 (Pending)
- Added Country field to at-con checkin, default to United States of America or previous attendee's country when
  2+ attendees are added to the same order
- Added badge reprint during at-con checkin
- Cleaned up Order List (/orders), added due and paid columns, username
- More attendee validation for at-con orders
- Fixed delete button for attendees in at-con orders
- Only show printer/till selections to users that can use them

Version 0.7.0 (6/9/2019)
- When creating database, add citext extension if it does not exist (needed to support case insensitive searches,
  previously created in a manual step)
- Fixed redirect bug when running behind load balancer that's using HTTPS
- Fixed NullPointerException when a request has no cookies
- Added Attendee Detail screen, add note functionality
- Added icons to Take Payment screen
- Added x and y offset on Printer selection page, support for storing offset in cookie
- Badge generation/printing from server
- Show badge image during checkin, added download badge PDF link
- Added reprint badge button
- Added badge test link /utility/testbadges.pdf that will generate badges with historically troublesome names

Version 0.6.0 (5/25/2019)
- Added Till Detail Report after discussion w/ Treasury Dept. This report will be included with each
  closed till.
- Added Force Password Change page/interceptor. This included a database schema change, making it incompatible
  with the previous version.
- Log current user with every message
- Added preferred pronoun to Attendee class and database table (schema change)
- Changed Right at_con_registration_set_fan_name to at_con_registration_specialty and allow users with that right to
  save attendees with very little validation (names and birthdates not required for Speciality badges)
- Added separate template for creating attendees without validation. It is used automatically for users with the 
  above right.
- Don't allow check or card payments for greater than the amount due
- Only show badges user has permission to see during at-con check in
- Added support for setting manual price at con with the attendee_override_price right
- Added support for Till Name saved by cookie client-side and saved when till session is closed (schema change)
- Set till name to "Attendee Import" during attendee import
- Added skeleton of view order/edit attendee pages
- Added option to set a "real" password for demo users, so the passwords won't have to be changed the first time
  each user logs in

Version 0.5 (4/9/2019)
- Initial "release" version. Not ready for production yet, but start tracking changes