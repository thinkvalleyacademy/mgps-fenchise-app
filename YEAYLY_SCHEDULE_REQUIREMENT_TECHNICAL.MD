Academic Planner Architecture (Recommended)
Academic Session
        │
        ▼
Academic Month
        │
        ▼
Academic Week
        │
        ▼
Academic Day
        │
        ▼
Class Planner
        │
        ▼
Periods (Dynamic)
        │
        ▼
Activities

Notice that Lesson is replaced with Class Planner because one day contains four different lesson plans (Playgroup, Nursery, LKG, UKG).

Database Design
academic_month
id
academic_year_id
month_no
month_name
theme
description
status
academic_week
id
month_id
week_no
title
learning_outcome
status
academic_day
id
week_id
day_no
date (optional)
day_name
class_planner

One record per class per day.

id

academic_day_id

class_id

theme

learning_outcome

teacher_note

status

created_by

updated_by

Example

Monday

Playgroup

↓

Planner

Monday

Nursery

↓

Planner

Monday

LKG

↓

Planner

Monday

UKG

↓

Planner
lesson_period

Instead of

P1
P2
P3
P4

store

id

planner_id

period_no

subject_id

teacher_id

title

objective

duration

display_order

Example

planner	Period
Nursery Monday	1
Nursery Monday	2
Nursery Monday	3
Nursery Monday	4

This allows future support for schools with 5 or 6 periods without schema changes.

period_activity
id

period_id

activity_type

activity_name

description

materials

duration

worksheet_id
User-Friendly Admin UI

Teachers should never fill the same information repeatedly.

Step 1 – Create Academic Year
2026-27

Create
Step 2 – Auto Generate Calendar
Generate Planner

Months : 12

Weeks : Auto

Days : Mon-Fri

Classes

✓ Playgroup

✓ Nursery

✓ LKG

✓ UKG

Periods

4

Generate

System automatically creates

12 Months

↓

52 Weeks

↓

260 Days

↓

1040 Class Planners

No manual creation.

Step 3 – Month Builder
April

Theme

[ Myself ]

Description

______________

Save
Step 4 – Week Builder
Week 1

Learning Outcome

______________

Save
Step 5 – Daily Planner (Spreadsheet Style)

Instead of opening one page at a time, provide a weekly grid.

Example:

Day	Class	P1	P2	P3	P4
Mon	Playgroup	+	+	+	+
Mon	Nursery	+	+	+	+
Mon	LKG	+	+	+	+
Mon	UKG	+	+	+	+
Tue	Playgroup	+	+	+	+

Clicking + opens a side drawer rather than a new page.

Period Drawer

Instead of a long form:

Subject

▼ English

Teacher

▼ Vandana

Topic

Letter A

Objective

________

Duration

45 min

Activity

________

Material

________

Assessment

________

Save

Use a side drawer.

+--------------------------------------+
| P1                                   |
+--------------------------------------+

Subject
▼ English

Teacher
▼ Vandana

Topic
_________________

Objective
_________________

Activity
_________________

Material
_________________

Assessment
_________________

[ Save ]

+--------------------------------------+

No page reload.

Smart Features
1. Copy Previous Day
Monday

[ Copy To ]

Tuesday

Wednesday

Thursday

Friday

One click copies the planner.

2. Copy to Another Class
Monday

Nursery

↓

Copy

↓

LKG

Useful when activities are similar.

3. Copy Entire Week
Week 1

↓

Copy

↓

Week 2

Then edit only what changes.

4. Duplicate Previous Academic Year
Academic Year

2026-27

↓

Duplicate

↓

2027-28

This is one of the biggest time savers.

5. Templates

Instead of creating P1 every time.

Template

Playgroup Monday

↓

Apply

Automatically fills

P1

Circle Time

P2

Math

P3

Activity

P4

Story
6. Drag & Drop

Move

P3

↓

P2

No editing required.

7. Progress Indicator
April

████████░░

80%

Week

Week 2

██████░░░░

60%
8. Quick Edit

Click directly in the table.

Topic

Letter A

↓

Letter B

Save automatically on blur.

9. Bulk Update
Select

Week 1

↓

Replace

English

↓

Phonics

Updates all matching periods.

10. Planner Timeline
2026

April

Week 1

Monday

Playgroup

P1 ✓

P2 ✓

P3 ✓

P4 ✓

Completed
Suggested Frontend Components
AcademicPlannerPage
│
├── SessionSelector
├── MonthSidebar
├── WeekTabs
├── WeekGrid
├── PlannerTable
├── PeriodDrawer
├── ActivityDrawer
├── ProgressBar
├── TemplateDialog
├── CopyPlannerDialog
├── BulkUpdateDialog
├── LearningOutcomePanel
└── AttachmentViewer
Recommended UX Flow
Select Academic Year
Click Generate Calendar (one-time setup)
Assign Monthly Theme
Define Weekly Learning Outcome
Fill the planner in the weekly spreadsheet view
Use templates, copy, and bulk edit to minimize repetitive work
Teachers simply open today's planner, complete the lesson, and mark it done

This approach minimizes clicks, avoids repetitive data entry, and is scalable for future features like AI lesson generation, parent communication, and printable weekly planners while remaining flexible enough to support different school schedules