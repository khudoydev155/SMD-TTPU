# Kotlin Coroutines Practice Solutions (15 Questions)

This repository contains solutions and detailed explanations for the 15 Coroutines practice questions (`kotlinx-coroutines-core:1.10.2`).

---

## Question 1. First Coroutine

### Context & Task
A cafe app confirms the order at once while coffee takes 1 second to prepare. Build a console app with `runBlocking` where a launched coroutine waits 1 sec with `delay` and prints `"Coffee ready"`, while main prints `"Order accepted"` immediately.

### Predicted Output
```
Order accepted
Coffee ready
```

### Actual Output
```
Order accepted
Coffee ready
```

### Explanation
`launch` schedules a new coroutine on the dispatcher queue without blocking the calling thread. Execution continues immediately in the main body, printing `"Order accepted"`. The child coroutine suspends for 1000 ms and then prints `"Coffee ready"`. `runBlocking` waits for all child coroutines in its scope to complete before terminating.

---

## Question 2. Three Baristas

### Context & Task
Three baristas make drinks concurrently on one thread: Espresso (300 ms), Tea (100 ms), Latte (200 ms). Compare non-blocking `delay()` vs blocking `Thread.sleep()`.

### Output with `delay()`
```
Espresso start
Tea start
Latte start
Tea end
Latte end
Espresso end
```

### Output with `Thread.sleep()`
```
Espresso start
Espresso end
Tea start
Tea end
Latte start
Latte end
```

### Thread Explanation
In the first case (`delay`), suspending frees the thread to allow other coroutines to execute while waiting; in the second case (`Thread.sleep`), the single underlying thread is blocked completely, forcing coroutines to execute strictly sequentially.

---

## Question 3. Chain of Suspend Functions

### Context & Task
Login flow: `loadUser` -> `checkPassword` -> `openProfile`. Demonstrate that suspend function calls execute strictly sequentially.

### Actual Output
```
Loading user...
Checking password...
Opening profile...
Profile opened successfully!
```

### Explanation
Suspending functions execute sequentially within a single coroutine unless explicitly launched in concurrent coroutine builders. Suspending pauses the execution of the coroutine at the call point and resumes only when the suspended function completes, preserving statement order.

---

## Question 4. Calling Suspend from Normal Code

### Context & Task
A teammate called a `suspend` function from a normal function, causing a compilation error. Reproduce error, state message, and provide 2 fixes with Android UI thread warning.

### Compile Error Message
```
Suspend function 'loadUserData' should be called only from a coroutine or another suspend function
```

### Fixes
1. **Fix 1:** Mark the calling function as `suspend`: `suspend fun fix1Caller() { ... }`
2. **Fix 2:** Launch a coroutine via a scope launcher (`CoroutineScope(Dispatchers.Default).launch { ... }`) or `runBlocking`.

### Android UI Thread Warning
> [!CAUTION]
> Never use `runBlocking` on the Android UI (main) thread. `runBlocking` blocks the underlying thread completely while waiting for coroutines to complete, causing the Android UI to freeze, leading to jank and Application Not Responding (ANR) dialogs.

---

## Question 5. Suspend is Not a Thread

### Context & Task
Disprove the myth that marking a function `suspend` moves work to a background thread. Identify the hidden compiler parameter.

### Actual Output
```
Thread name before call: main @coroutine#1
Thread name inside suspend function: main @coroutine#1
Disproved belief: marking a function 'suspend' does NOT change the execution thread!
```

### Proof / Disproof
The output shows the function executed on the exact same thread (`main`). The `suspend` keyword alone does not perform context or thread switching.

### Hidden Compiler Parameter
The Kotlin compiler transforms every `suspend` function using Continuation-Passing Style (CPS) by appending a hidden parameter: `completion: Continuation<T>` (e.g. `Continuation<Unit>`).

---

## Question 6. Ten Downloads, One Thread

### Context & Task
Launch 10 download coroutines on a single thread under `runBlocking`. Show that all 10 start before any finish. Decide if this is concurrency or parallelism.

### Actual Output
```
download 1 started on thread main @coroutine#2
download 2 started on thread main @coroutine#3
download 3 started on thread main @coroutine#4
download 4 started on thread main @coroutine#5
download 5 started on thread main @coroutine#6
download 6 started on thread main @coroutine#7
download 7 started on thread main @coroutine#8
download 8 started on thread main @coroutine#9
download 9 started on thread main @coroutine#10
download 10 started on thread main @coroutine#11
download 1 finished on thread main @coroutine#2
download 2 finished on thread main @coroutine#3
...
download 10 finished on thread main @coroutine#11
```

### Concurrency vs. Parallelism
This is **concurrency**, NOT parallelism. Concurrency means managing multiple tasks at the same time by interleaving execution during suspension on a single thread. Parallelism requires multiple CPU threads/cores physically executing multiple tasks at the exact same millisecond.

---

## Question 7. Order of Launch

### Context & Task
Under `runBlocking`, print `"1"`, `launch` a coroutine (`"2"`, `delay(100)`, `"3"`), print `"4"`, `delay(50)`, print `"5"`.

### Predicted & Actual Output
```
1
4
2
5
3
```

### Explanation
When `launch` is called, the child coroutine is added to the event queue of the main thread dispatcher. The main coroutine continues executing until it yields/suspends (`delay(50)`). Thus, `"4"` prints before the thread yields to start the child coroutine (`"2"`). At 50 ms, main resumes and prints `"5"`. At 100 ms, the child coroutine resumes and prints `"3"`.

---

## Question 8. Parent Waits for Child

### Context & Task
Parent launches child (200 ms attachment compression). Parent prints `"parent body done"`. Child prints `"child done"`. Main joins parent and prints `"report completed"`.

### Actual Output
```
parent body done
child done
report completed
```

### Structured Concurrency Rule
Structured concurrency dictates that a parent coroutine scope automatically delays its own completion until all of its child coroutines have completed. `parentJob.join()` suspends main until the parent job AND all its children finish.

---

## Question 9. Screen Closed, Requests Cancelled

### Context & Task
Parent launches 2 child coroutines (each with `try/delay(1000)/finally`). Parent is cancelled after 100 ms with `cancelAndJoin()`.

### Actual Output
```
Request 1 cleanup executed
Request 2 cleanup executed
end
```

### Explanation
Cancelling a parent `Job` automatically cancels all child jobs attached to its `CoroutineScope`. When cancelled, the child coroutines receive a `CancellationException` at their suspension points (`delay`), triggering their `finally` blocks for immediate resource cleanup.

---

## Question 10. Profile Ready

### Context & Task
Load avatar (200 ms) and settings (100 ms) inside `coroutineScope`. Print `"Profile ready"` after scope. Launch an extra coroutine after scope and observe main exit print.

### Actual Output
```
Settings loaded
Avatar loaded
Profile ready
end of main
Extra coroutine finished
```

### Explanation
`coroutineScope` is a suspending scope builder that suspends the caller until all children inside it complete. Therefore `"Profile ready"` prints only after both avatar and settings are loaded. In contrast, the `launch` after the scope starts asynchronously, allowing `"end of main"` to execute before the extra coroutine finishes.

---

## Question 11. Reusable Counter

### Context & Task
Create an extension function `CoroutineScope.startCounter(name, maxCount)` returning `Job`. Start two counters inside `coroutineScope`.

### Actual Output
```
launched
Counter A: 1
Counter B: 1
Counter B: 2
Counter A: 2
Counter A: 3
```

### Receiver Explanation
The `CoroutineScope` receiver gives the extension access to the caller's `CoroutineContext` and parent `Job`, attaching the new coroutine to the caller's lifecycle in compliance with structured concurrency.

---

## Question 12. Returns at Once or Waits

### Context & Task
Compare a `CoroutineScope` extension (returns immediately) with a `suspend` function using `coroutineScope` (waits until all complete).

### Actual Output
```
--- Calling Helper 1 (CoroutineScope Extension) ---
Line right after Helper 1 call (Returns immediately!)
Background Greeter: Hello Alice

--- Calling Helper 2 (Suspend function with coroutineScope) ---
Sequential Greeter: Hello Bob
Sequential Greeter: Hello Charlie
Line right after Helper 2 call (Waits until all done!)
```

### Explanation
1. `CoroutineScope.startBackgroundGreeter`: Non-suspending extension that launches a coroutine fire-and-forget style and returns a `Job` immediately.
2. `suspend fun greetAllSequentially`: Suspending function containing `coroutineScope` that suspends execution until all child coroutines inside it complete.

---

## Question 13. Five Builders Cheat Sheet

### Program Output
```
launch returned: StandaloneCoroutine{Active}@12345
async returned: DeferredCoroutine{Active}@67890
coroutineScope returned: Result from coroutineScope
runBlocking returned: Result from async
```

### Lecture Cheat Sheet Table

| Builder | Returns | Blocks Thread? | Suspends Caller? | Waits for Children? |
| :--- | :--- | :--- | :--- | :--- |
| `launch` | `Job` | No | No | Outer scope waits |
| `async` | `Deferred<T>` | No | No | Outer scope waits |
| `runBlocking` | `T` (result) | **Yes** | No | **Yes** |
| `coroutineScope` | `T` (result) | No | **Yes** | **Yes** |
| `withContext` | `T` (result) | No | **Yes** | **Yes** |

---

## Question 14. Job or Deferred

### Context & Task
Demonstrate why `launch` cannot return a calculation result (`6 * 7`), whereas `async` returns `Deferred<T>` with `.await()`.

### Actual Output
```
launch returned Job object: StandaloneCoroutine{Active}@... (Cannot extract calculation result!)
async returned Deferred<Int>, await() produced result: 42
```

### Explanation
- `Job`: Represents a background task with a lifecycle (active, completed, cancelled). It yields no return value (`Unit`).
- `Deferred<T>`: A subtype of `Job` that represents a lightweight future holding a deferred result of type `T`, retrieved via `.await()`.

---

## Question 15. Three Exports in a Scope

### Context & Task
Export X (300 ms), Y (100 ms), Z (200 ms) in `coroutineScope`. Print `"Start"` before and `"All done"` after scope without using `join()` or delays in main.

### Required & Actual Output
```
Start
Y
Z
X
All done
```

### Explanation
`coroutineScope` automatically suspends until all child coroutines complete. As Y finishes in 100 ms, Z in 200 ms, and X in 300 ms, their outputs print in completion order (`Y`, `Z`, `X`). Once all three finish, `coroutineScope` completes and `"All done"` prints.
