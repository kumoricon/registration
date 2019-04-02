How To....
==========




Arrange Buttons on a Form
-------------------------

To keep forms consistent, arrange buttons in the following order:

```
Save    Cancel    Reset                              Delete
```

This also means that pressing the Enter key in a form field will perform the default Submit action "Save", which is
usually what people expect.

For example:
```$html
<div class="form-group row">
    <div class="col-sm-10">
        <input class="btn btn-primary" name="action" id="save" type="submit" value="Save" />
        <a th:href="'/reg/atconorder/' + ${order.id}" class="btn btn-secondary">Cancel</a>
        <input class="btn btn-secondary" type="reset" value="Reset" />
    </div>
    <div class="col-sm-2">
        <input class="btn btn-danger" th:if="${attendee.id != null}" type="submit" name="action" value="Delete" />
    </div>
</div>

```



Handle Dates, Times, and Timestamps
-----------------------------------

Dates and times that represent a specific instant in time should be stored in the database in UTC, but should 
generally be shown to the user in the user's time zone (currently PST is hardcoded).

The only notable exception is the attendee's date of birth, which is actually a LocalDate (stored without timezone
information). Though technically not true, we generally want to assume that someone born on April 3rd at 1:00 AM on
the east coast treats their birthday as April 3rd, even though they were actually born on April 2nd at 10:00 PM in
the pacific time zone.

More than you ever wanted to know about time:

- [Choosing the right object](http://mattgreencroft.blogspot.com/2014/12/java-8-time-choosing-right-object.html)
- [What's the difference between Instant and LocalDateTime](https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime/32443004)

