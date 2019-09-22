# Change History
Version 0.8.3 (pending)

Version 0.8.2 (9/22/2019)
- Made order details optional on till report
- Added badge image preview and PDF download to on At-Con print badge page
- Added badge type management screen for enabling/disabling badge types

Version 0.8.1 (9/7/2019)
- Changed login/logout pages to use site template/theme
- Added attendee edit with override from another user that has edit right
- Add warning if till session is open when logging out
- Updated prices for 2019
- Added parental consent form checkbox to pre-reg check in
- Copy emergency contact info to parent fields when "Parent is Emergency Contact" is checked on at-con speciality screen
- Copy zip code from previous attendee in the same order during at-com registration

Version 0.8.0 (8/18/2019)
- Tightened up formatting on at-con registration forms
- Removed Computers/Printers from the menu
- Don't show View Order link on Attendee Detail page if user doesn't have manage_orders right
- Added Check In link on Attendee Detail page
- Import preferred pronoun during attendee import
- Fixed text on reset password screen; users will not be logged out after setting a password
- Added Guest Badge import and screens

Version 0.7.2 (7/30/2019)
- Added a "Back to Search" button on the Attendee Detail page that links back to either the search by name or
  search by badge page as appropriate
- Added pronoun field on regular at-con attendee checkin screen
- Fixed permissions: staff could not view attendees after searching or view badges when printing badges
- Added Attendee edit screen
- Disable browser autocomplete on forms
- Automatically format phone numbers

Version 0.7.1 (6/30/2019)
- Added Country field to at-con checkin, default to United States of America or previous attendee's country when
  2+ attendees are added to the same order
- Added badge reprint during at-con checkin
- Cleaned up Order List (/orders), added due and paid columns, username
- More attendee validation for at-con orders
- Fixed delete button for attendees in at-con orders
- Only show printer/till selections to users that can use them
- Use regular input field for birthdates (so mobile devices don't pop up calendars and make you scroll through
  every month to pick a date)

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