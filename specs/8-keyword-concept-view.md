# Contains the UI specification for the ConceptKeywordView
# The 3 entities SubKeyword/Keyword/Concept are tied with child-parent-grandchildren relation ship
 - View divided in 2 panels
 - Left panel list all the Concept, while the right panel will display the hierarchy under this Concept
 - In this right panel, we have a way to add Keyword and SubKeyword
 - An horizontal tree display, to illustrate the hierarchy. With line going from one element to another one.
 - Each node has only one parent, but can have several children
 - The hierarchy is obvious based on the Entity definition
 - One Keyword can have up to 100 SubKeyword
 - Double click on a node will enable to edit the node, by opening a small form at the bottom
 - Deletion doesn't delete the childs but only make them orphans
 - We need to have a way to display the orphans to link them properly
 - Be creative !
