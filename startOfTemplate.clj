(BOUNDING-BOX (0 0) (200 300))


; Figure: bar chart
(DRAW black 
    ; 'Scala' bar commands
    (TEXT-AT (9 177.2) Scala)
    (TEXT-AT (61.6 177.2) 5)
    (fill red (RECTANGLE (30 174) (56.6 182)))
    
    ; 'More Scala' bar commands
    (TEXT-AT (9 189.2) More Scala)
    (TEXT-AT (82.8 189.2) 9)
    (fill blue (RECTANGLE (30 186) (77.8 194)))
    
    ; 'Prolog' bar commands
    (TEXT-AT (9 201.2) Prolog)
    (TEXT-AT (120 201.2) 16)
    (fill yellow (RECTANGLE (30 198) (115 206)))
    
    ; X-axis numbers
    (TEXT-AT (30 162) 0)
    (TEXT-AT (56.6 162) 5)
    (TEXT-AT (83 162) 10)
    (TEXT-AT (109.7 162) 15)

    ; Frame
    (TEXT-AT (53.5 150) "What do you expect from life?")
    (RECTANGLE (30 170) (130 210)))


; Figure: pie chart
(DRAW black 
    ; Frame
    (TEXT-AT (45.2 16.1) Popularity of Programming Languages)
    (FILL gray (CIRCLE (75 75) 40))
    (CIRCLE (75 75) 40)
    
    ; 'Haskell' section commands
    (TEXT-AT (117 91) Haskell)
    (TEXT-AT (102.6 84) 11%)
    (LINE (75 75) (105.8 100.5))
    
    ; 'Lisp' section commands
    (TEXT-AT (107.2 108) Lisp)
    (TEXT-AT (95.2 97.5) 4%)
    (LINE (75 75) (98.5 107.4))
    
    ; 'Scala' section commands
    (TEXT-AT (70.8 120.2) Scala)
    (TEXT-AT (72.5 106.2) 20%)
    (LINE (75 75) (51.5 107.4))
    
    ; 'Other' section commands
    (TEXT-AT (23.7 95.2) Other)
    (TEXT-AT (43.5 87.8) 16%)
    (LINE (75 75) (35.1 72.5))
    
    ; 'Prolog' section commands
    (TEXT-AT (71.5 28.2) Prolog)
    (TEXT-AT (73.5 42.2) 49%)
    (LINE (75 75) (115 74)))
