# Pipes in the Desert
Software Project Laboratory - BME (BMEVIIIAB02)

## Overview
Pipes in the Desert is a strategy-based simulation game developed as part of the Software Project Laboratory course. The game models a dynamic water distribution system where two opposing teams (Plumbers and Saboteurs) compete to either maximize water delivery or increase water loss.


## Project Structure
```
Pipes in the Desert/
│
├── code/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── app/           # Application source code
│   │   └── test/
│   │       ├── java/
│   │       │   └── app/           # Prototype test harness
│   │       └── resources/
│   │           └── test-cases/    # Input/output scenario files
│   ├── scripts/                   # Build and test automation scripts
│   └── target/                    # Compiled classes (generated)
│
├── documentation/                 # Milestone PDFs and reports
├── assets/                        # Assets are put in a folder
├── archive/                       # Archived historical/legacy snapshots
└── README.md
```


## Documentation Status (as of May 23)

Completed:
- 00 - Cover
- 02 - Project Definition
- 03 - Analysis Model (First Version)
- 04 - Analysis Model (Final Version)
- 05 - Skeleton Plans
- 06 - Skeleton Program
- 07 - Prototype Concept
- 08 - Detailed Prototype Plans
- 10 - Prototype Program 
- 11 - Plans for GUI
- 13 - GUI program 

Upcoming
- 14 - Summary (May 29)


## Build & Run

Requirements:
- Java (JDK 17+)

(1) Build:
```powershell
cd code
.\scripts\compile.ps1
```

(2) Run prototype tests:
```powershell
cd code
.\scripts\run-tests.ps1
```

(3) Run GUI:
```powershell
cd code
.\scripts\run-gui.ps1
```

(4) VS Code:
```
Open code/src/main/java/app/Main.java and press F5
```


## Team 
This repository contains the project of Team A under the supervision of Dr. Balla Katalin (balla@iit.bme.hu).

| Name                   | GitHub                              | Code   | Email |
|------------------------|---------------------------------------------|--------|-------|
| Muhammad Ibrahim Shoeb | [ibrahimify](https://github.com/ibrahimify) | OZLVV3 | muhammadibrahimshoeb@gmail.com |
| Arda Gecegörür         | [Arda-23](https://github.com/ardagecegorur)       | EKQNAK | ardagecegorur@gmail.com |
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



