(BOUNDING-BOX (0 0) (200 300))


; Figure: bar chart
(DRAW black 
    ; Frame
    (RECTANGLE (30 170) (130 210))
    (TEXT-AT (53.5 150) "What do you expect from life?")
    
    ; 'Scala' bar commands
    (RECTANGLE (30 174) (56.6 182))
    (TEXT-AT (61.6 177.2) 5)
    (TEXT-AT (9 177.2) Scala)
    
    ; 'More Scala' bar commands
    (RECTANGLE (30 186) (77.8 194))
    (TEXT-AT (82.8 189.2) 9)
    (TEXT-AT (9 189.2) More Scala)
    
    ; 'Prolog' bar commands
    (RECTANGLE (30 198) (115 206))
    (TEXT-AT (120 201.2) 16)
    (TEXT-AT (9 201.2) Prolog)
    
    ; X-axis numbers
    (TEXT-AT (30 162) 0)
    (TEXT-AT (56.6 162) 5)
    (TEXT-AT (83 162) 10)
    (TEXT-AT (109.7 162) 15))


; Figure: pie chart
(DRAW black 
    ; Frame
    (FILL gray (CIRCLE (75 75) 40))
    (CIRCLE (75 75) 40)
    (TEXT-AT (45.2 16.1) Popularity of Programming Languages)
    
    ; 'Haskell' section commands
    (LINE (75 75) (105.8 100.5))
    (TEXT-AT (102.6 84) 11%)
    (TEXT-AT (117 91) Haskell)
    
    ; 'Lisp' section commands
    (LINE (75 75) (98.5 107.4))
    (TEXT-AT (95.2 97.5) 4%)
    (TEXT-AT (107.2 108) Lisp)
    
    ; 'Scala' section commands
    (LINE (75 75) (51.5 107.4))
    (TEXT-AT (72.5 106.2) 20%)
    (TEXT-AT (70.8 120.2) Scala)
    
    ; 'Other' section commands
    (LINE (75 75) (35.1 72.5))
    (TEXT-AT (43.5 87.8) 16%)
    (TEXT-AT (23.7 95.2) Other)
    
    ; 'Prolog' section commands
    (LINE (75 75) (115 74))
    (TEXT-AT (73.5 42.2) 49%)
    (TEXT-AT (71.5 28.2) Prolog))
