# Meeting Notes

## Key Info

---

Date : 05-12-2025

Time: 13:45 - 14:30

Location: Drebbelweg hall 2

Chair: Andrei Radu

Minute Taker: Jakub Pieczonka

Attendees: 6 (Anando was late around 20 min)

Start Time: 13:47

## Agenda Items

---

### 1. Opening by chair (0 min)

### 2. Check-in: How is everyone doing? (1 min)

### 3. Announcements by the team (2 min)

-   Who is missing reviews and merges? (No one)
-   Reviews numbers don't have to be evenly split - TA
-   IDEA: Make reviews more precise, e.g. code lines mention

### 4. Approval of the agenda - Does anyone have any additions? (1 min)

No additions.

### 5. Approval of last minutes - Did everyone read the last minutes? Do you approve of these? (1 min)

Approved with praise by Arthur and Kuba.

### 6. Announcements by the TA (0 min)

No announcments by the TA.

### 7. Presentation of the current app to TA (4 min)

-   Progress: UI, search bar, most of basic requirements - changing ingredients to be implemented.
-   Bug: deleting names of preparation steps.
-   Feedback: "amazing".

### 8. Recap of the last two meetings and checking on personal progress (5 min)

-   **Andrei**: search bar, removed circular dependency, multi-keyword query works (done)
-   **Arthur**: UI/database refactoring, preparation steps editing (done)
-   **Anando**: cloning, adding a recipe (almost done)
-   **Elliot**: Didn't finish UI testing as it was more difficult than expected. He will do something else before today - 05.12. (not done)
-   **Szymon**: Frontend basic controller testing. It was quite complicated. (done)
-   **Kuba**: Exporting recipe to a pdf, testing all backend controllers. (done)

TA mentioned such advanced testing is not required (i.e. not need for UI testing)

### 9. Questions for the TA (8 min)

-   Should there be more than 1 reviewer per MR?
    -   It is good practice to have more. Comment section is required.
-   Must a merge be done before Friday in order to count for the knockout criteria?
    -   Commits dates do count
    -   Merges count in the date that there are mergeable
-   Should we have a description for milestones?
    -   No. Milestones per week suffice.
-   Should milestones be weekly? What do they actually represent?
    -   Skipped - was answered on Mattermost already.
-   Is a issue containging 3 lines of user story too big?
    -   We should use subtasks.
    -   E.g. One issue for the search bar group with one subtask for each user story
    -   TA still hasn't commented on the quality of our issues
-   A commit during the midterm week counts for the knockout criteria?
    -   Contribtutions not counted.

### 10. Discuss - What user stories do we want to cover next? (16 min)

-   **Anando**: Add/Delete recipes, Refresing after change, Cloning.
-   **Arthur**: Staring recipes, favorites, database startup change.
-   **Szymon**: Parts of nutritional value feature.
-   **Kuba**: PreperationSteps testing, parts of nutritional value feature.
-   **Andrei**: ServerUtils testing.
-   **Elliot**: Today: Ingredient frontend. Next week: parts of shopping listfeature.

### 11. Decision Making - Assigning tasks for every memeber (0 min)

Included in 10.

### 12. Brainstorm - Extra possible user stories (0 min)

Skipped, partially discussed in 10.

### 13. Summarize action points: Who, what, when? (2 min)

-   Everyone shortly described their responsibilities.
-   Next minute-taker - Elliot
-   Szymon, Andrei: join online on the 18th of December
-   Anando: joins online on the 9th of January

### 14. Feedback round : What went well and what can be improved next time? (1 min)

-   Good meeting
-   For future agendas: combine 10 and 11 and possible 12.

### 15. Planned meeting duration != actual duration? Where/why did you misestimate? (1 min)

Estimates were very good.

### 16. Question round: Does anyone have anything to add before the meeting closes? (0 min)

No questions.

### 17. Closure

### TODO after meeting

-   Ingredient editing design
-   Milestone creation
-   Issues creation
-   Database startup fixes
-   Go over Anando's changes
