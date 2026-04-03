# Collatz Explorer

A console Java application for exploring the **Collatz (3n + 1) conjecture**: generate sequences, inspect stopping times and peaks, analyze ranges of starting values, and compare two starting numbers side by side.

## Features

- **Sequence generation** — Build the trajectory from a starting positive integer (optional full path printing).
- **Stopping time** — How many steps until the sequence reaches 1.
- **Peak value tracking** — Maximum value encountered along the path.
- **Range analysis** — Summarize metrics across an inclusive range of starting values.
- **Comparison mode** — Compare two starting values (e.g. stopping time and peaks).

## The Collatz Conjecture

The Collatz Conjecture, also known as the **3n + 1 problem**, defines a sequence for any positive integer. If the number is odd, multiply it by 3 and add 1. If it is even, divide it by 2.

For example, starting at **5**: 5 is odd → 3×5+1 = **16**; 16 is even → **8** → **4** → **2** → **1**. After reaching **1**, the sequence enters the repeating **4, 2, 1** loop.

It is still an open question in mathematics whether every positive integer eventually reaches 1, or whether some counterexample could diverge or cycle elsewhere.

## Requirements

- [JDK](https://openjdk.org/) 17 or newer (uses modern Java language features)

## How to Run

From the repository root:

javac -d out -sourcepath src/main/java src/main/java/collatz/app/CollatzExplorerApp.java
java -cp out collatz.app.CollatzExplorerApp