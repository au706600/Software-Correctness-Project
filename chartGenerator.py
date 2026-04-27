from dataclasses import dataclass
from math import cos, sin, pi


@dataclass
class Part:
    label: str
    amount: float


def roundToText(val: float) -> str:
    decimals = 1
    if abs(round(val) - round(val, decimals)) < 1/10**decimals:
        return str(int(val))
    else:
        return str(round(val, decimals))


def point(xy: tuple[float, float]) -> str:
    return f'({roundToText(xy[0])} {roundToText(xy[1])})'


def circle(xy: tuple[float, float], r: float) -> str:
    return f'(CIRCLE {point(xy)} {r})'


def rectangle(xy1: tuple[float, float], xy2: tuple[float, float]) -> str:
    return f'(RECTANGLE {point(xy1)} {point(xy2)})'


def line(xy1: tuple[float, float], xy2: tuple[float, float]) -> str:
    return f'(LINE {point(xy1)} {point(xy2)})'


def draw(color: str, commandos: list[str]) -> str:
    out: list[str] = [] 
    out.append(f'(DRAW {color} ')
    for commando in commandos:
        out.append('    '+commando)
    out[-1] += ')'
    return '\n'.join(out)
    

def fill(color: str, command: str) -> str:
    return f'(FILL {color} {command})'


def text(xy: tuple[float, float], t: str) -> str:
    return f'(TEXT-AT {point(xy)} {t})'


def boundingBox(xy1: tuple[float, float], xy2: tuple[float, float]) -> str:
    return f'(BOUNDING-BOX {point(xy1)} {point(xy2)})'


def barChart(xy1: tuple[float, float], xy2: tuple[float, float], parts: list[Part], label: str, baseColor: str) -> str:
    cmds: list[str] = []

    width = xy2[0] - xy1[0]
    height = xy2[1] - xy1[1]
    
    cmds.append('; Frame')
    cmds.append(text((xy2[0] - xy1[0] - len(label)*1.5, xy1[1] - width*0.2), label))
    cmds.append(rectangle(xy1, xy2))

    maxAmount = max(part.amount for part in parts)

    barHeight = height/(len(parts)*1.5 + 0.5)

    barLowY = xy1[1]

    xBarNumberJump = 5
    textYScaleRatio = -0.1
    barLengthScale = 0.85

    for i, part in enumerate(parts[::-1]):
        barLengthRatio = part.amount / maxAmount
        if i==0:
            barLowY += barHeight/2
        else:
            barLowY += barHeight*1.5
        barEndX = xy1[0] + width*barLengthRatio*barLengthScale
        textYPos = barLowY + barHeight * (0.5 + textYScaleRatio)
        cmds.append('')
        cmds.append(f"; '{part.label}' bar commands")
        cmds.append(text((xy1[0] - width*0.21, textYPos), part.label))
        cmds.append(text((barEndX + width*0.05, textYPos), str(part.amount)))
        cmds.append(rectangle((xy1[0], barLowY), (barEndX, barLowY + barHeight)))
    
    cmds.append('')
    cmds.append('; X-axis numbers')
    for i in range(int(maxAmount / xBarNumberJump) + 1):
        xVal = xy1[0] + i*xBarNumberJump/maxAmount*width*barLengthScale
        cmds.append(text((xVal, xy1[1] - width*0.08), str(i*xBarNumberJump)))


    return draw(baseColor, cmds)


def pieChart(xy: tuple[float, float], radius: float, parts: list[Part], label: str, baseColor: str, fillColor: str) -> str:
    x, y = xy
    cmds: list[str] = []

    totalAmount = sum(part.amount for part in parts)

    def addLine(angle: float):
        cmds.append(line((x, y), (x + radius*cos(angle), y + radius*sin(angle))))

    def addText(angle: float, ratioScale: float, sideScale: int, text_: str):
        cmds.append(text((x + radius*ratioScale*cos(angle) - len(text_)*0.85, y + radius*(ratioScale + sideScale*abs(sin(angle)*0.15))*sin(angle) - 0.85), text_))

    cmds.append('; Frame')
    addText(-pi/2, 1.6, -1, label)
    cmds.append(fill(fillColor, circle((x, y), radius)))
    cmds.append(circle((x, y), radius))

    oldRad = 0
    angle = 0
    for part in parts:
        ratio = part.amount/totalAmount
        angle += ratio*2*pi
        textAngle = (angle + oldRad)/2
        percent = ratio*100

        cmds.append('')
        cmds.append(f"; '{part.label}' section commands")

        addText(textAngle, 1.3, -1, part.label)
        addText(textAngle, 0.8, 0, str(round(percent))+'%')

        if len(parts) > 1:
            addLine(angle)

        oldRad = angle

    return draw(baseColor, cmds)


barChartParts = [
    Part('Prolog', 16),
    Part('More Scala', 9),
    Part('Scala', 5),
]


piChartParts = [
    Part('Haskell', 11),
    Part('Lisp', 4),
    Part('Scala', 20),
    Part('Other', 16),
    Part('Prolog', 49),
]


def makeFiguresCode() -> str:
    out: list[str] = []
    out.append(boundingBox((0, 0), (200, 300)))
    out.append('')
    out.append('')
    out.append('; Figure: bar chart')
    out.append(barChart(
        (30, 170), (130, 210),
        barChartParts,
        '"What do you expect from life?"',
        "black"))
    out.append('')
    out.append('')
    out.append('; Figure: pie chart')
    out.append(pieChart(
        (75, 75), 40,
        piChartParts,
        "Popularity of Programming Languages",
        "black", 'gray'))
    out.append('')
    return '\n'.join(out)


if __name__ == '__main__':
    figuresCode = makeFiguresCode()

    # print(figuresCode)

    with open('startOfTemplate.clj', "w") as file:
        file.write(figuresCode)


# py chartGenerator.py; sbt run 

