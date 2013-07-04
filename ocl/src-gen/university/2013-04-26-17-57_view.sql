-- Generated from OCL2SQL generator(Dresden OCL)
-- Generation time: 2013-04-26-17-57
-- Template: Standard(SQL)
-- Modus: typed


-- Context: Person
-- Expression: inv tudOclInv1: self.supervisor.grade.value > self.grade.value 
CREATE OR REPLACE VIEW tudOclInv1 AS
(SELECT * FROM OV_Person AS self
WHERE NOT (((SELECT temp1.value
FROM OV_Grade AS temp1
INNER JOIN (SELECT FK_grade,PK_Person FROM OV_Person) AS temp2 ON temp1.PK_Grade = temp2.FK_grade
WHERE temp2.PK_Person = self.FK_supervisor) > (SELECT temp3.value
FROM OV_Grade AS temp3
WHERE temp3.PK_Grade = self.FK_grade))));

-- Context: Faculty
-- Expression: inv tudOclInv2: self.subFacility->size() >= 2 
CREATE OR REPLACE VIEW tudOclInv2 AS
(SELECT * FROM OV_Faculty AS self
WHERE NOT (((SELECT CASE
  WHEN COUNT(temp1.PK_Facility) IS NULL THEN 0
  ELSE COUNT(temp1.PK_Facility)
END
FROM OV_Facility AS temp1
WHERE temp1.FK_superFacility = self.PK_Facility) >= 2)));

-- Context: Faculty
-- Expression: /** All subfacilities must be an Institute** Used patterns: QUERY, BASIC TYPE, CLASS AND ATTRIBUTE, COMPLEX PREDICATE*/inv tudOclInv3: self.subFacility->forAll(f:Facility | f.oclIsTypeOf(Institute)) 
CREATE OR REPLACE VIEW tudOclInv3 AS
(SELECT * FROM OV_Faculty AS self
WHERE NOT (NOT EXISTS((SELECT temp1.PK_Facility
FROM OV_Facility AS temp1
WHERE (temp1.FK_superFacility = self.PK_Facility AND NOT(EXISTS(
  SELECT temp2.PK_Facility FROM OV_Institute AS temp2
  WHERE temp2.PK_Facility = temp1.PK_Facility)))))));

-- Context: Employee
-- Expression: inv tudOclInv4:	((self.grade.name = 'diploma') implies (self.taxClass = 'tc1'))						and ((self.grade.name = 'doctor') implies (self.taxClass = 'tc2'))						and ((self.grade.name = 'professor') implies (self.taxClass = 'tc3')) 
CREATE OR REPLACE VIEW tudOclInv4 AS
(SELECT * FROM OV_Employee AS self
WHERE NOT ((((NOT ((SELECT temp1.name
FROM OV_Grade AS temp1
WHERE temp1.PK_Grade = self.FK_grade) = 'diploma') OR (self.taxClass = 'tc1')) AND (NOT ((SELECT temp2.name
FROM OV_Grade AS temp2
WHERE temp2.PK_Grade = self.FK_grade) = 'doctor') OR (self.taxClass = 'tc2'))) AND (NOT ((SELECT temp3.name
FROM OV_Grade AS temp3
WHERE temp3.PK_Grade = self.FK_grade) = 'professor') OR (self.taxClass = 'tc3')))));

-- Context: Facility
-- Expression: inv tudOclInv5: self.member->includes(self.headOfFacility) 
CREATE OR REPLACE VIEW tudOclInv5 AS
(SELECT * FROM OV_Facility AS self
WHERE NOT (self.FK_headOfFacility IN
  ((SELECT temp1.FK_member
FROM ASS_member_owner AS temp1
WHERE temp1.FK_owner = self.PK_Facility))));

-- Context: Paper
-- Expression: inv tudOclInv6: ((self.purpose = 'Diplom') and (self.inProgress = true)) implies (self.author->forAll(p:Person | p.oclIsTypeOf(Student))) 
CREATE OR REPLACE VIEW tudOclInv6 AS
(SELECT * FROM OV_Paper AS self
WHERE NOT ((NOT ((self.purpose = 'Diplom') AND (((self.inProgress = 1) AND (1=1)) OR (NOT (self.inProgress = 1) AND NOT (1=1)))) OR NOT EXISTS((SELECT temp1.FK_author
FROM ASS_author_papers AS temp1
WHERE (temp1.FK_papers = self.PK_Paper AND NOT(EXISTS(
  SELECT temp2.PK_Person FROM OV_Student AS temp2
  WHERE temp2.PK_Person = temp1.FK_author))))))));

-- Context: Faculty
-- Expression: inv tudOclInv7: self.headOfFacility.grade.name = 'professor' 
CREATE OR REPLACE VIEW tudOclInv7 AS
(SELECT * FROM OV_Faculty AS self
WHERE NOT (((SELECT temp1.name
FROM OV_Grade AS temp1
INNER JOIN (SELECT FK_grade,PK_Person FROM OV_Person) AS temp2 ON temp1.PK_Grade = temp2.FK_grade
WHERE temp2.PK_Person = self.FK_headOfFacility) = 'professor')));

-- Context: Grade
-- Expression: inv tudOclInv8: Set{'none','diploma','doctor','professor'}->includes(self.name) 
CREATE OR REPLACE VIEW tudOclInv8 AS
(SELECT * FROM OV_Grade AS self
WHERE NOT (self.name IN
  ('none'
UNION
'diploma'
UNION
'doctor'
UNION
'professor')));

-- Context: Employee
-- Expression: inv tudOclInv9_1: (self.grade.name = 'doctor') implies (self.papers->select(p:Paper | p.purpose = 'Dissertation')->size() = 1) 
CREATE OR REPLACE VIEW tudOclInv9_1 AS
(SELECT * FROM OV_Employee AS self
WHERE NOT ((NOT ((SELECT temp1.name
FROM OV_Grade AS temp1
WHERE temp1.PK_Grade = self.FK_grade) = 'doctor') OR ((SELECT CASE
  WHEN COUNT(temp2.PK_Paper) IS NULL THEN 0
  ELSE COUNT(temp2.PK_Paper)
END
FROM OV_Paper AS temp2
INNER JOIN (SELECT FK_papers,FK_author FROM ASS_author_papers) AS temp3 ON temp2.PK_Paper = temp3.FK_papers
WHERE (temp3.FK_author = self.PK_Person AND (temp2.purpose = 'Dissertation'))) = 1))));

-- Context: Employee
-- Expression: -- rejectinv tudOclInv9_2: (self.grade.name = 'doctor') implies (self.papers->reject(p:Paper | p.purpose = 'Dissertation')->size() > 0) 
CREATE OR REPLACE VIEW tudOclInv9_2 AS
(SELECT * FROM OV_Employee AS self
WHERE NOT ((NOT ((SELECT temp1.name
FROM OV_Grade AS temp1
WHERE temp1.PK_Grade = self.FK_grade) = 'doctor') OR ((SELECT CASE
  WHEN COUNT(temp2.PK_Paper) IS NULL THEN 0
  ELSE COUNT(temp2.PK_Paper)
END
FROM OV_Paper AS temp2
INNER JOIN (SELECT FK_papers,FK_author FROM ASS_author_papers) AS temp3 ON temp2.PK_Paper = temp3.FK_papers
WHERE (temp3.FK_author = self.PK_Person AND NOT((temp2.purpose = 'Dissertation')))) > 0))));

-- Context: Student
-- Expression: inv tudOclInv10_1: self.papers->count(self.currentPaper) = 1 
CREATE OR REPLACE VIEW tudOclInv10_1 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (((SELECT CASE
  WHEN COUNT(temp1.FK_papers) IS NULL THEN 0
  ELSE COUNT(temp1.FK_papers)
END
FROM ASS_author_papers AS temp1
WHERE (temp1.FK_author = self.PK_Person AND (temp1.FK_papers = self.FK_currentPaper))) = 1)));

-- Context: Student
-- Expression: -- includesinv tudOclInv10_2: self.papers->includes(self.currentPaper) 
CREATE OR REPLACE VIEW tudOclInv10_2 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (self.FK_currentPaper IN
  ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))));

-- Context: Student
-- Expression: -- excludesinv tudOclInv10_3: not(self.papers->excludes(self.currentPaper)) 
CREATE OR REPLACE VIEW tudOclInv10_3 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (NOT(self.FK_currentPaper NOT IN
  ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person)))));

-- Context: Student
-- Expression: -- includesAllinv tudOclInv10_4: self.papers->includesAll(self.papers) 
CREATE OR REPLACE VIEW tudOclInv10_4 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (NOT EXISTS (
  ((SELECT temp2.FK_papers
FROM ASS_author_papers AS temp2
WHERE temp2.FK_author = self.PK_Person))
  EXCEPT
  ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person)))));

-- Context: Student
-- Expression: -- excludesAllinv tudOclInv10_5: not(self.papers->excludesAll(self.papers)) 
CREATE OR REPLACE VIEW tudOclInv10_5 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (NOT(NOT EXISTS (
  ((SELECT temp2.FK_papers
FROM ASS_author_papers AS temp2
WHERE temp2.FK_author = self.PK_Person))
  INTERSECT
  ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))))));

-- Context: Student
-- Expression: -- isEmptyinv tudOclInv10_6: not(self.papers->isEmpty()) 
CREATE OR REPLACE VIEW tudOclInv10_6 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (NOT(NOT EXISTS ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person)))));

-- Context: Student
-- Expression: -- notEmptyinv tudOclInv10_7: self.papers->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv10_7 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))));

-- Context: Student
-- Expression: -- sizeinv tudOclInv10_8: self.papers->size() > 0 
CREATE OR REPLACE VIEW tudOclInv10_8 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (((SELECT CASE
  WHEN COUNT(temp1.FK_papers) IS NULL THEN 0
  ELSE COUNT(temp1.FK_papers)
END
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person) > 0)));

-- Context: Student
-- Expression: -- suminv tudOclInv10_9: self.salaries->sum() = 300 
CREATE OR REPLACE VIEW tudOclInv10_9 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((CASE
  WHEN SUM(self.salaries) IS NULL THEN 0
  ELSE SUM(self.salaries)
END = 300)));

-- Context: Student
-- Expression: -- intersectioninv tudOclInv11_1: self.papers->intersection(self.supervisor.papers)->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv11_1 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (((SELECT temp2.FK_papers
FROM ASS_author_papers AS temp2
WHERE temp2.FK_author = self.FK_supervisor) INTERSECT
  (SELECT temp1.FK_papers
  FROM ASS_author_papers AS temp1
  WHERE temp1.FK_author = self.PK_Person)))));

-- Context: Student
-- Expression: -- includinginv tudOclInv11_2: self.papers->including(self.currentPaper)->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv11_2 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person) UNION
  (self.FK_currentPaper)))));

-- Context: Student
-- Expression: -- excludinginv tudOclInv11_3: self.papers->excluding(self.currentPaper)->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv11_3 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person) EXCEPT
  (self.FK_currentPaper)))));

-- Context: Student
-- Expression: -- unioninv tudOclInv11_4: self.papers->union(self.supervisor.papers)->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv11_4 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person) UNION
  (SELECT temp2.FK_papers
  FROM ASS_author_papers AS temp2
  WHERE temp2.FK_author = self.FK_supervisor)))));

-- Context: Student
-- Expression: -- union (sequence)inv tudOclInv12_1: self.papers->asSequence()->union(self.supervisor.papers->asSequence())->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv12_1 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))
  UNION
  (SELECT temp1.FK_papers, (SELECT MAX(SEQNO) FROM ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))) AS SEQNO
   FROM (SELECT temp2.FK_papers
FROM ASS_author_papers AS temp2
WHERE temp2.FK_author = self.FK_supervisor)))));

-- Context: Student
-- Expression: -- including (sequence)inv tudOclInv12_2: self.papers->asSequence()->including(self.currentPaper)->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv12_2 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))
  UNION
  (SELECT self.FK_currentPaper, ((SELECT MAX(SEQNO) FROM ((SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person))) + 1) AS SEQNO))));

-- Context: Student
-- Expression: -- excluding (sequence)inv tudOclInv12_3: self.papers->asSequence()->excluding(self.currentPaper)->notEmpty() 
CREATE OR REPLACE VIEW tudOclInv12_3 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (EXISTS (SELECT self.FK_currentPaper,
  (SELECT COUNT(*)+1 FROM (
    SELECT self.FK_currentPaper, SEQNO
    FROM self.FK_currentPaper
    WHERE NOT (PK_Paper = self.FK_currentPaper)
  ) WHERE SEQNO < s.SEQNO) AS SEQNO
  FROM (
    SELECT self.FK_currentPaper, SEQNO
    FROM (SELECT temp1.FK_papers
FROM ASS_author_papers AS temp1
WHERE temp1.FK_author = self.PK_Person)
    WHERE NOT (PK_Paper = self.FK_currentPaper)
  ))));

-- Context: Student
-- Expression: -- size (basic type)inv tudOclInv13_1: self.firstName.size() > 0 
CREATE OR REPLACE VIEW tudOclInv13_1 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((LENGTH(self.firstName) > 0)));

-- Context: Student
-- Expression: -- concat (basic type)inv tudOclInv13_2: self.firstName.concat(self.lastName).size() = self.firstName.size() + self.lastName.size() 
CREATE OR REPLACE VIEW tudOclInv13_2 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((LENGTH(self.firstName || self.lastName) = (LENGTH(self.firstName) + LENGTH(self.lastName)))));

-- Context: Student
-- Expression: -- toUpper (basic type)inv tudOclInv13_3: self.firstName.toUpperCase().size() = self.firstName.size() 
CREATE OR REPLACE VIEW tudOclInv13_3 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((LENGTH(UPPER(self.firstName)) = LENGTH(self.firstName))));

-- Context: Student
-- Expression: -- toLower (basic type)inv tudOclInv13_4: self.firstName.toLowerCase().size() = self.firstName.size() 
CREATE OR REPLACE VIEW tudOclInv13_4 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((LENGTH(LOWER(self.firstName)) = LENGTH(self.firstName))));

-- Context: Student
-- Expression: -- substring (basic type)inv tudOclInv13_5: self.firstName.substring(1, 3).size() = 3 
CREATE OR REPLACE VIEW tudOclInv13_5 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((LENGTH(SUBSTRING(self.firstName, 1, 3 - 1 + 1)) = 3)));

-- Context: Student
-- Expression: -- abs (basic type)inv tudOclInv14_1: self.age.abs() > 0 
CREATE OR REPLACE VIEW tudOclInv14_1 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((ABS(self.age) > 0)));

-- Context: Student
-- Expression: -- floor (basic type)inv tudOclInv14_2: self.age.floor() > 0 
CREATE OR REPLACE VIEW tudOclInv14_2 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((FLOOR(self.age) > 0)));

-- Context: Student
-- Expression: -- round (basic type)inv tudOclInv14_3: self.age.round() > 0 
CREATE OR REPLACE VIEW tudOclInv14_3 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((ROUND(self.age) > 0)));

-- Context: Student
-- Expression: -- max (basic type)inv tudOclInv14_4: self.age.max(1000) = 1000 
CREATE OR REPLACE VIEW tudOclInv14_4 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((CASE
  WHEN self.age > 1000 THEN self.age
  ELSE 1000
END = 1000)));

-- Context: Student
-- Expression: -- min (basic type)inv tudOclInv14_5: self.age.min(-1) = -1 
CREATE OR REPLACE VIEW tudOclInv14_5 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((CASE
  WHEN self.age < -1 THEN self.age
  ELSE -1
END = -1)));

-- Context: Student
-- Expression: -- div (basic type)inv tudOclInv14_6: self.age.div(1000) < 1 
CREATE OR REPLACE VIEW tudOclInv14_6 AS
(SELECT * FROM OV_Student AS self
WHERE NOT (((self.age / 1000) < 1)));

-- Context: Student
-- Expression: -- mod (basic type)inv tudOclInv14_7: self.age.mod(1000) = self.age 
CREATE OR REPLACE VIEW tudOclInv14_7 AS
(SELECT * FROM OV_Student AS self
WHERE NOT ((self.age - ((self.age / 1000) * 1000) = self.age)));