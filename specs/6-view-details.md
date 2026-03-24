# View details
-**For each entity, we need a List view, a Read view, and a Edit/Create view.**
-**The Read/Edit/Create could be the same display, except it is editable and display the actual fields values**
-**Every view take the same size on the screen, no additional pop up window**


# List View details
-**For every List view, we need to display a few columns for each object, details below**
-**Author List : firstName, lastName**
-**Concept List : name, keywords.value**
-**Keyword List : value**

-**Language List : name**
-**Manuscript List : uniqueId, titre, language, referenceManuscript**
-**Name List : name**
-**Publication List : key, author, publicationTitle, shortTitle**
-**Variant List : variant, name**

-**Simple click on List view show the Read view**
--**Double click on List view show the Edit view**



# Read/Create/Edit view details
-**For every entity, the id field is auto-generated and not editable**
-**For every entity, every field except the auto-generated id field is visible and editable in the edit/create view**
-**If many fields are displayed, you can optimize some UI distribution to make the view easy to understand**
-**The view take the same space as the list view**

-**Every Set< ? @DbRef> should be displayed as a multi-select dropdown in the edit/create view, allowing users to select multiple related entities. The options in the dropdown should be populated with the existing entities of the related type. Multi-select combo box**
-**We also need a way to limit the space taken by this combo-box, to handle case where we have too many entities choice to link**
-**We need a filter to filter the option offered by the combo-box, add a text field on the combo-box that will filter on the available entities**
-**Synchronise the combo-boxes with the field values, in order to be able to actually use it.**