package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Question 11 (1): the value is passed to Child, so Parent reads the state.
@Composable
fun Parent() {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { Log.d("Skip", "Parent: composition") }

    Column {
        Button(onClick = { count++ }) { Text("Add") }
        Child(count)
        Sibling()
    }
}

@Composable
fun Child(count: Int) {
    SideEffect { Log.d("Skip", "Child: composition") }
    Text("Count: $count")
}

@Composable
fun Sibling() {
    SideEffect { Log.d("Skip", "Sibling: composition") }
    Text("I never change")
}

// Question 11 (2): the value is passed as a lambda, the state is read inside Child.
@Composable
fun ParentWithLambda() {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { Log.d("Skip", "Parent: composition") }

    Column {
        Button(onClick = { count++ }) { Text("Add") }
        ChildWithLambda(count = { count })      // pass a lambda that returns the value
        Sibling()
    }
}

@Composable
fun ChildWithLambda(count: () -> Int) {
    SideEffect { Log.d("Skip", "Child: composition") }
    Text("Count: ${count()}")                   // the state is read here, not in Parent
}
