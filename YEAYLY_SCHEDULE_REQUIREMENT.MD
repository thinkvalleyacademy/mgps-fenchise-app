Applicable Classes: Playgroup, Nursery, LKG, UKG

1. Objective

The Academic Planner module will provide teachers and administrators with a structured annual lesson plan aligned with NEP 2020.

The planner will allow navigation from

Academic Year → Month → Week → Day → Period

Teachers should never need to search through documents. They should be able to open today's lesson in one click.

2. Users
Role	Permission
Super Admin	Full CRUD
School Admin	Full CRUD
Principal	View & Edit
Coordinator	View & Edit
Teacher	View Assigned Class
Parent (Future)	View Theme & Home Activity only
3. Navigation Hierarchy
Academic Planner

2026-27
│
├── April
│      │
│      ├── Week 1
│      │      │
│      │      ├── Monday
│      │      ├── Tuesday
│      │      ├── Wednesday
│      │      ├── Thursday
│      │      └── Friday
│      │
│      ├── Week 2
│      ├── Week 3
│      └── Week 4
│
├── May
├── June
...
└── March
4. Calendar UI
Year View
Academic Session

2026-27

□□□□□□□□□□□□□□□□□□□□

APR
MAY
JUN
JUL
AUG
SEP
OCT
NOV
DEC
JAN
FEB
MAR

Click Month

↓

Month View
APRIL

Theme
"Myself"

Week 1
Week 2
Week 3
Week 4

Click Week

↓

Week View
Week 1

Theme

Myself

Monday
Tuesday
Wednesday
Thursday
Friday

Click Day

↓

5. Day View
Monday

Theme

Myself

Playgroup
Nursery
LKG
UKG

Teacher selects class

↓

6. Daily Lesson Screen

Example

Date

06 April 2026

Class

Nursery

Theme

Myself

Learning Outcome

Children identify themselves.

----------------------------------

Prayer & Assembly

Thought

Prayer

National Anthem

Attendance

----------------------------------

P1

English

Letter A

Conversation

My Name

Material

Alphabet Flashcards

Teacher Notes

....

----------------------------------

P2

Math

Number 1

Red Colour

Material

Counting Blocks

Teacher Notes

....

----------------------------------

Snack Break

----------------------------------

P3

Activity

Self Portrait

Material

Crayons

Mirror

Worksheet

----------------------------------

P4

Story

"My Family"

Rhyme

Good Morning Song

Outdoor Activity

Ball Passing

----------------------------------

Assessment

□ Recognizes Letter

□ Counts Objects

□ Speaks Own Name

□ Participates
7. Period Data Structure

Each period should contain the following.

Field	Required
Subject	Yes
Learning Area	Yes
Learning Objective	Yes
Teacher Instructions	Yes
Materials Required	Yes
Activity Steps	Yes
Assessment Method	Yes
Homework / Home Connection	Optional
Worksheet Attachment	Optional
Video Link	Optional
8. Weekly Screen
Week 1

Theme

Myself

------------------------------------------------

Monday

Playgroup

P1
P2
P3
P4

Nursery

P1
P2
P3
P4

LKG

P1
P2
P3
P4

UKG

P1
P2
P3
P4

------------------------------------------------

Tuesday

....

## Timetable / Academic Planner Section
The timetable module must follow the Academic Planner hierarchy and support a seamless month/week/day/class/period workflow.

- Academic Session → Academic Month → Academic Week → Academic Day → Class Planner → Periods → Activities
- The system must support auto-generation of the planner so teachers do not create the same structure manually:
  - 12 months
  - 52 weeks
  - 260 days
  - 1 planner per class per day
  - support for 4 pre-primary classes: Playgroup, Nursery, LKG, UKG

### Planner UI behavior
- Year view shows the academic session and month tiles.
- Month view displays the selected month, theme, and week list.
- Week view shows the selected week, theme, and days Monday–Friday.
- Day view shows the selected day, theme, and available classes.
- Weekly planner must present a spreadsheet-style grid with rows for each day/class and columns for dynamic periods:
  - Day | Class | P1 | P2 | P3 | P4 (and future additional periods)
- Each period cell should show a placeholder (e.g. +) when empty and open a side drawer to edit details.

### Period drawer / lesson details
Each period entry must capture the following fields:
- Subject
- Learning Area
- Learning Objective
- Teacher Instructions
- Materials Required
- Activity Steps
- Assessment Method
- Homework / Home Connection (optional)
- Worksheet Attachment (optional)
- Video Link (optional)

### Data model requirements
The timetable should be backed by the following entities:
- `academic_month`
  - academic_year_id, month_no, month_name, theme, description, status
- `academic_week`
  - month_id, week_no, title, learning_outcome, status
- `academic_day`
  - week_id, day_no, date, day_name
- `class_planner`
  - academic_day_id, class_id, theme, learning_outcome, teacher_note, status, created_by, updated_by
- `lesson_period`
  - planner_id, period_no, subject_id, teacher_id, title, objective, duration, display_order
- `period_activity`
  - period_id, activity_type, activity_name, description, materials, duration, worksheet_id

This timetable section must align with the technical architecture in `YEAYLY_SCHEDULE_REQUIREMENT_TECHNICAL.MD` and provide the same planner and weekly grid behavior for teachers.

9. Learning Outcome Mapping

Every lesson should map to one or more learning domains.

Domain	Example
Language & Literacy	Letter recognition
Early Numeracy	Counting
Cognitive Development	Sorting
Physical Development	Running
Fine Motor	Tracing
Gross Motor	Jumping
Social Emotional	Sharing
Creative Arts	Painting
Environmental Awareness	Plants
Life Skills	Hygiene
10. Planner Entity Structure
Academic Year

↓

Month

↓

Theme

↓

Week

↓

Day

↓

Class

↓

Period

↓

Lesson
11. Lesson Data Model
Academic Year
Month
Week
Day

Class

Theme

Learning Outcome

Period

Subject

Topic

Objective

Materials

Activity

Teacher Notes

Assessment

Attachments

Video URL

Worksheet PDF
12. Dashboard

Teacher Dashboard

Today's Planner

Theme

Myself

Playgroup

Completed

75%

Nursery

Pending

LKG

Pending

UKG

Completed

Attendance

26/28

Upcoming Event

Red Colour Day
13. Reports

Administrator should be able to view:

Lesson completion percentage
Weekly completion
Monthly completion
Teacher-wise completion
Class-wise completion
Missed lessons
Theme coverage
Learning outcome coverage
Assessment completion
14. Future Enhancements
AI-generated lesson suggestions based on theme.
Printable weekly planner (PDF).
Teacher notes and reflections.
Parent view with "This Week We Learned".
Photo and video evidence upload for classroom activities.
Homework and announcements linked to daily lessons.
Push notifications for special events and celebration days.
Suggested Database Schema
AcademicYear
 ├── Month
 │     ├── Theme
 │     │      ├── Week
 │     │      │      ├── Day
 │     │      │      │      ├── Class
 │     │      │      │      │      ├── P1
 │     │      │      │      │      ├── P2
 │     │      │      │      │      ├── P3
 │     │      │      │      │      └── P4
 │     │      │      │      │
 │     │      │      │      └── Assessment
 │     │      │      │
 │     │      │      └── Home Activity
 │     │      │
 │     │      └── Learning Outcomes
 │     │
 │     └── Celebration Days
 │
 └── Reports

This hierarchy is scalable, supports multiple academic sessions, and provides a clean, intuitive navigation experience for teachers and administrators while keeping the focus on daily classroom execution. It also leaves room for future features such as digital assessments, printable planners, and parent engagement.