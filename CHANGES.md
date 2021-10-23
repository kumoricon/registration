# Change History
Version 1.3.0 (pending)
- Added event registration system export
- Added accessibility sticker toggle to attendees and staff
- Use titleBadgeShort field from website for staff titles
- Generate child and youth badges for test badge PDFs
- Handle 2021 attendee export fields, ignore any canceled attendees
- Load mascot image from badge resource path instead of badge image path
- Generate badge numbers randomly instead of generating them per-user
- Display legal name on at-con registration order screen
- Search in-line registrations by legal name in addition to preferred name
- Automate staff user creation
- Fix "force password change" setting applying to training users
- Hide badge list if empty
- Remove signature requirement from staff check-in

Version 1.2.0 (7/22/2021)
- Update schema creation script with order_uuid column
- Update default configuration files with in-line registration paths
- In-line registration screens

Version 1.1.0 (6/22/2021)
- Search staff list server-side for performance on slow clients
- Added each user's total logged in time for the displayed day to the Login History report
- Time period is displayed when you hover over a cell in the Login History Report
- Added autocomplete dropdown to staff search
- Switch to Jetty from Tomcat, which increased performance, especially for file uploads
- Upload staff photos as JPG instead of PNG
- Save staff photos and signatures with the name {name}-{id}-photo/signature-{timestamp}.jpg/png
- Added configurable run-time settings (schema change) and configuration screen
- Remove emergency contact info from pre-reg check in screen
- Added payment information to order detail screen
- Add configuration option to require staff photo and signature
- Add checks to ensure authorization number is 10 characters or less.
- Added Staff Checked In by Department report with chart
- Null values in Attendee and Staff badge DTOs will initialize with empty string values instead.
- Added director-only Revoke Membership button (schema change)
- Make sure names on blacklist are unique
- Added attendee and staff export to CSV
- Don't require phone or email for Child badges
- Added required note to Attendee Edit screen, saves to Attendee History
- Added "Ask Me My Pronouns" to available preferred pronouns
- Allow fewer digits in check number field (at-con payment)
- Added payment timestamp to till detail report
- Reworked rights on Current User Info screen
- Added Order Hand Off screen and buttons for at-con orders. (schema change)
- Removed email field from at-con check in screen for regular attendees
- Automatically capitalize first, last, and parent, and emergency contact names on at-con registration form
- Added In-Line Registration check in workflow (schema change)
- Added hero cards for different tasks on the homepage
- Enabled server compression, browser caching headers, and HTTP/2
- Homepage and attendee forms more mobile friendly
- Upgraded Spring Boot to 2.1.1 -> 2.3.0, Java 11 -> 14
- Added View Till Report links to Administration / Till Sessions
- Upgraded Spring Boot 2.3.0 -> 2.4.4, Java 14 -> 15, Junit 5, other library versions
- Upgraded Java 15 -> 16

Version 1.0.1 (11/14/2019)
- Staff check in: don't search staff list until 2 characters have been entered in search (for performance)
- Staff check in: don't redirect away from step 4 when printing badge front (since they'll need to print 
  badge back as well)
- Staff check in: added toggle to show checked in/not checked in staff
- Add autocomplete for attendee search

Version 1.0.0 (11/11/2019)
- Renamed "Staff" report to "Users" on menu
- Fixed bug where override user wouldn't work on attendee edit screen
- Add note when Attendee is edited with override
- Add "Back" button on Printer select screen to take user back to whatever
  URL they were on before selecting the printer
- Add "Print Badge Front" and "Print Badge Back" buttons on guest and staff pages
- Made default password and force password change setings configurable at run time

Version 0.8.5 (11/8/2019)
- MSO role permissions cleanup
- Fixed pronouns in staff import
- Improved signature pad detection
- Fixed redirect to non-existent page when printing staff badges
- Mark guests as checked in during attendee import
- Add support for online purchases of single day badges
- Set staff login photo resolution to 800x600
- Added download badge PDF by badge type

Version 0.8.4 (10/26/2019)
- Added attendance report
- Added legal name to staff check in list
- Added T-shirt size to final staff check-in screen
- Take check number on Check Payment screen
- Added signature pad support. SigPlusExtLite SDK & Browser Extension must be installed.
- Renamed Staff Report to Users Report
- Added Staff to Check in by Badge report
- Added checked in count to Staff Check In screen
- Added utility/testbadges screen
- Added badge printing layouts for 2019
- Added .deb package creator for installation
- Added Military Discount weekend badge type

Version 0.8.3 (10/9/2019)
- Changed "Registration" in menu bar to link to home page 
- Removed extra whitespace on orders/X/attendees/X/edit screen 
- Add Fan Name field to orders/X/attendees/X/edit screen
- Added Staff check in (work in progress)

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
