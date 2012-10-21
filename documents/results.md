CS290B-HW2
==========

ASSIGNMENT 2: A BASIC COMPUTE FARM

by Karl Lopker and Geoffrey Douglas

Results
-------

Mandelbrot
----------
	51, 167, 88, 104, 91, 244, 337, 669, 36, 121, 223, 307, 240, 436, 584, 611, 30, 69, 153, 265, 307, 535, 650, 852, 26, 38, 101, 169, 250, 352, 406, 490, 25, 38, 50, 189, 111, 174, 195, 608, 38, 38, 34, 100, 94, 55, 381, 474, 73, 30, 56, 60, 76, 61, 237, 251, 26, 62, 127, 223, 225, 244, 380, 595

	Tasks Done! Computation average time: 223

	Total Time: 14774

Sales Man
---------
	9751, 9928, 8805, 9514, 9217, 9122, 8626, 9193, 9180, 9057, 8535

	Computation average time: 9175

	Total Time: 54206


Analysis
--------
The average time for the Mandelbrot computation was 223ms, but the times varied wildly. This is likely do to the actual set calculation times and not with the distributed computing system. Some sections of the set have higher iteration counts which would require more calculations and thus take longer.

On the other hand, the sales man tasks all completed about the same time. This is because each task was doing the same amount of work on different inputs. The tasks took so long the overhead of loading class files cannot be seen.


