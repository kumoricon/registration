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
