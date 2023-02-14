bugs:


to work on:
  - recycler view not visible when launched, it comes when navigating back
  
  


- COMPLETED -
    - swipe to delete
    - delete all courses option with separate destination
    - calculate the gpa
  
    - data didn't appear in edit course fragment --> 
        REASON: name of the state was different than
                the one in the navigation graph (subject || course)
  
    - the gpa should be updated automatically as the data changed --> 
        REASON: it was called only when the fragment is recreated; becasuse it was in viewCreated,
                it should be inside the observer of the data 


- version 2.0 -
  - make the courses categorized into terms
  - calculate gpa for each term, and cumulative of all terms
  - enhance ui by using androidx
  - custom theming