# A.I. Workshop

This repo features two separate projects which implement A.I. algorithms developed based on the Artificial Intelligence course [@cse.uoi.gr](https://www.cs.uoi.gr/).<br><br>
 
### PROJECT 1: Minimax Board Game
<br>

The first project is a turn-based board game where the user plays against the computer. It features a GUI for user options at the start of the game and for the game itself. Both the user and the computer occupy one block of the board and can move up to two blocks in any direction, horizontally included. The origin block and any crossed block are made unaivalable, meaning they cannot be crossed again. When one's turn arrives with no available moves the opposing player wins. The project also outlines front and back-end developement.

The [Minimax algorithm](https://en.wikipedia.org/wiki/Minimax) is implemented and used by the computer to determine the next best move. The algorithm uses recursion to simulate all the possible future scenarios of any current state and eventually picks the best move available as determined by the Minimax tree. As the simulation can get very resource-heavy its depth can be limited by the SIMULATION_LIMIT constant in the Logic Class.

<br>

### Usage:

To play simply run `play.jar`.

Or alternatively, at the root of the package, compile with:<br>
    `javac minimax/Logic.java`

and run with:<br>
    `java minimax/Logic`

jar executable can be created with the following command:<br>
    `jar cvfe play.jar minimax.Logic minimax/*.class`

<br>
<img alt="demo img here"  src="https://raw.githubusercontent.com/ch-ant/ai-workshop/main/minimax/demo.png"/>
<br><br>
<br><br>

### PROJECT 2: Search Algorithms Comparison
<br>

The second project is a comparison between the search algorithms, [Uniform Cost Search (UCS)](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs) and [A* (A-star)](https://en.wikipedia.org/wiki/A*_search_algorithm). 
Both algorithms are used to find the path with the lowest cost (shortest path) from an initial state to a final state. The initial state is a shuffled sequence of integers from 1 to N split by a comma (N is the total number of integers and duplicate values are not allowed). A final state is the sequence of integers sorted in ascending order. For instance, for N=5 the final state would be `[1,2,3,4,5]`. The transition operators (allowed transitions on any state) are symbolized as <b><i>T(k)</b></i> where <b><i>2<=k<=N</b></i> and they function as follows. The current state is split in two parts. The first part contains the first <b><i>k</b></i> integers which are reversed. The second part contains the remaining integers which are left intact. For instance, applying the transition operator <b><i>T(3)</b></i> to the state `[3,4,1,5,2]` would result in the state `[1,4,3,5,2]`. 

For the A* algorithm a heuristic function was approximated. A detailed report on the heuristic function and the comparison between the two algorithms can be found in the report (GR). The general conclusion is that for the given search problem the A* algorithm is more efficient than the UCS algorithm. The difference is much more obvious for higher N values and more shuffled lists of integers.


<br>

### Usage:

compile command: 
`javac Search.java`

run command: 
`java Search <search algorithm> <initial state>`

`<search algorithm>`: ucs OR alphastar 
`<initial state>`: shuffled sequence of ints from 1 to N split by a comma (N is the total number of ints) (duplicate values are not allowed)

run examples:
`java Search ucs 4,3,5,2,1`
`java Search alphastar 4,3,5,2,1`

<br><br>

### LANGUAGE

Java 14.0.2
