# Pipes in the Desert
Software Project Laboratory - BME (BMEVIIIAB02)

## Overview
Pipes in the Desert is a strategy-based simulation game developed as part of the Software Project Laboratory course. The game models a dynamic water distribution system where two opposing teams (Plumbers and Saboteurs) compete to either maximize water delivery or increase water loss.


## Project Structure
```
Pipes-in-the-Desert/
│
├── src/                    # Maven config (coming soon)
│   ├── main/
│   │   ├── java/                # Source code
│   │   └── resources/           # Assets (config, images, etc.)
│   └── test/
│       └── java/                # Unit tests 
│
├── documentation/
│   ├── 0. cover.doc
│   ├── 1. Requirements Documentation.pdf
│   ├── 2. Analysis Model.pdf
│   ├── 3. Analysis_Model_Final.pdf
│   ├── 4. Planning_The_Skeleton.pdf
│   ├── 5. Skeleton Program.pdf
│   ├── 6. Concept_of_Prototype.pdf
│   ├── 7. Detailed_Prototype_Plans.pdf
│   ├── 10_Prototype_Program.pdf          # Coming soon
│   ├── 11_Plans_for_GUI.pdf              # Coming soon
│   ├── 13_GUI_Program.pdf                # Coming soon
│   ├── 14_Summary.pdf                    # Coming soon
│
├── docs/                       # Static_Structure_Diagram
│
├── pom.xml                    # Maven config
├── README.md
└── .gitignore
```


## Documentation Status (as of April 29)

Completed:
- 00 - Cover
- 02 - Project Definition
- 03 - Analysis Model (First Version)
- 04 - Analysis Model (Final Version)
- 05 - Skeleton Plans
- 06 - Skeleton Program
- 07 - Prototype Concept
- 08 - Detailed Prototype Plans

Upcoming:
- 10 - Prototype Program (May 4)
- 11 - Plans for GUI (May 5)
- 13 - GUI program (May 26)
- 14 - Summary (May 29)



## Build & Run

Requirements:
- Java (JDK 17+)
- Maven

(1) Build:
```
mvn compile
```

(2) Run:
```
mvn exec:java
```

(3) VS Code:
```
Open Main.java and press F5
```


## Team 
This repository contains the project of Team A under the supervision of Dr. Balla Katalin (balla@iit.bme.hu).

| Name                   | GitHub                              | Code   | Email |
|------------------------|---------------------------------------------|--------|-------|
| Muhammad Ibrahim Shoeb | [ibrahimify](https://github.com/ibrahimify) | OZLVV3 | muhammadibrahimshoeb@gmail.com |
| Arda Gecegörür         | [Arda-23](https://github.com/Arda-23)       | EKQNAK | ardagecegorur@gmail.com |
| Ilgın Tunç             | [ilgintunc](https://github.com/ilgintunc)   | TDQWFF | ilgintunc11@gmail.com |
| Muhammad Hameez Khan   | [Hameez1khan](https://github.com/Hameez1khan)| TFBB32 | hameezkhan993@gmail.com |
| Yahya Akhrikhar        | [yahyaakh](https://github.com/yahyaakh)     | K20UXP | yahyaakhrikhar02@gmail.com |
| Aasif Mohd             |                                             | OI6VE6 | aasifuk1122@gmail.com |



## Schedule Highlights

Prototype presentation: May 5  
Final presentation: May 26  
Final submission (Summary): May 29  

Weekly deadline: Monday 14:30  


## Meetings

| Type       | Day       | Time        | Location           |
|------------|-----------|-------------|--------------------|
| Online     | Tuesday   | 18:00       | Discord            |
| In-person  | Saturday  | 09:00 - 18:00 | Corvinus Library   |

## Description

The system simulates a water transport network consisting of pipes, pumps, springs, and cisterns.

- Plumbers maintain and extend the system
- Saboteurs disrupt flow and cause leaks
- The game runs in turns
- The winner is determined by comparing delivered vs lost water


## Notes

- All documentation follows course milestone structure
- Both draft and final versions of documents are preserved
- Project is developed incrementally according to course workflow

## Links

- [Course Webpage](https://www.iit.bme.hu/oktatas/tanszeki_targyak/BMEVIIIAB02)  
- [Problem Definition](https://www.iit.bme.hu/targyak/BMEVIIIAB02/problem-definition)  
- [Deadlines](https://www.iit.bme.hu/targyak/BMEVIIIAB02/schedule)  
- [Document Templates](https://www.iit.bme.hu/file/1006/document-templates)  


