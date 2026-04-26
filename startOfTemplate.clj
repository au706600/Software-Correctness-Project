(BOUNDING-BOX (0 0) (300 300))

;bar chart

(RECTANGLE (15 5) (100 50))
(TEXT-AT (15 3) 0)
(TEXT-AT (35 3) 5)
(TEXT-AT (55 3) 10)
(TEXT-AT (75 3) 15)

(TEXT-AT (40 -5) "What do you expect from life?")

(fill b (RECTANGLE (16 45) (80 40)))
(draw b (TEXT-AT (8 42) Prolog))
(draw b (TEXT-AT (82 42) 16))

(fill r (RECTANGLE (16 30) (54 25)))
(draw r (TEXT-AT (5 27) More Scala))
(draw r (TEXT-AT (56 27) 9))

(fill k (RECTANGLE (16 15) (35 10)))
(TEXT-AT (8 12) Scala)
(TEXT-AT (37 12) 5)



;pie chart
(CIRCLE (50 100) 30)
(TEXT-AT (40 60) Popularity of Programming Languages )

(LINE (50 100) (50 130))
(TEXT-AT (60 115) 25%)
(TEXT-AT (70 125) Scala)


(LINE (50 100) (80 100))
(TEXT-AT (40 115) 25%)
(TEXT-AT (20 120) Java)

(LINE (20 100) (80 100))
(TEXT-AT (30 90) 15%)
(TEXT-AT (15 85) Haskell)


(LINE (30 78) (50 100))
(TEXT-AT (55 85) 35%)
(TEXT-AT (75 80) Python)


