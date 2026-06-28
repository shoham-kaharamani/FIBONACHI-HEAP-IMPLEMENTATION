# Fibonacci Heap Implementation in Java

A complete, high-performance implementation of a **Fibonacci Heap** over positive integers from scratch in Java. This project showcases advanced algorithm design, focus on theoretical amortized time complexities, and complex pointer-based memory structures.

## Features
* **Amortized Efficiency:** Supports $O(1)$ amortized time operations for `insert`, `findMin`, and `meld`, making it highly efficient for network routing and optimization algorithms (like Dijkstra's shortest path).
* **Successive Linking & Consolidation:** Features a robust structural consolidation process (`consolidate`) utilizing a temporary bucket array logic to merge trees of equal rank upon minimum deletion.
* **Cascading Cuts:** Implements full cascading cut logic during key decreases (`decreaseKey`), maintaining structural parameters and tree bounds efficiently based on custom cut limits.
* **Doubly Linked Pointer Architecture:** Manages intricate circular doubly linked lists for both root levels and child layers, tracking nodes dynamically via explicit Java pointer management.

## Time Complexities
* **Insert / FindMin / Meld:** $O(1)$ Worst Case, $O(1)$ Amortized
* **DecreaseKey:** $O(n)$ Worst Case, $O(1)$ Amortized
* **DeleteMin / Delete:** $O(n)$ Worst Case, $O(\log n)$ Amortized

## Technologies Used
* **Java** (Object-Oriented Programming, Memory Management)

## Code Structure
* `FibonacciHeap`: Main interface orchestration class managing heap status, operations, and tracking metric logs (total cuts/links).
* `HeapNode`: A nested static entity structure encapsulating local keys, positional metadata, ranks, and structural pointers.
