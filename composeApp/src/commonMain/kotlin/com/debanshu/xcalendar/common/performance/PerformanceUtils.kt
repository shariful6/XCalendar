package com.debanshu.xcalendar.common.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Performance monitoring utilities for tracking recompositions and optimizing performance
 */

/**
 * Tracks recomposition count for a composable.
 * Use this in debug builds to identify excessive recompositions.
 *
 * Example:
 * ```
 * @Composable
 * fun MyComposable() {
 *     RecompositionCounter("MyComposable")
 *     // ... rest of composable
 * }
 * ```
 */
@Composable
fun RecompositionCounter(tag: String) {
    val counter = remember { RecompositionState() }
    SideEffect {
        counter.count++
        println("🔄 Recomposition #${counter.count} for: $tag")
    }
}

/**
 * Measures the time taken for a composable to compose.
 * Use this to identify slow composables.
 *
 * Example:
 * ```
 * @Composable
 * fun MyComposable() {
 *     MeasureCompositionTime("MyComposable") {
 *         // ... composable content
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalTime::class)
@Composable
inline fun MeasureCompositionTime(
    tag: String,
    content: @Composable () -> Unit,
) {
    val startTime = remember { Clock.System.now() }
    content()
    SideEffect {
        val endTime = Clock.System.now()
        val duration = endTime - startTime
        if (duration.inWholeMilliseconds > 16) { // More than one frame (16ms)
            println("⚠️ Slow composition: $tag took ${duration.inWholeMilliseconds}ms")
        }
    }
}

/**
 * Logs when a lambda is created, useful for tracking unstable lambdas
 * that cause recompositions.
 *
 * Example:
 * ```
 * val onClick = rememberLambda("onClick") {
 *     // handle click
 * }
 * ```
 */
@Composable
inline fun <T> rememberLambda(
    tag: String,
    crossinline lambda: () -> T,
): () -> T =
    remember {
        { lambda() }
    }

/**
 * Helper class to track recomposition state
 */
class RecompositionState {
    var count = 0
}

/**
 * Debug utility to log state changes
 */
@Composable
fun <T> LogStateChange(
    tag: String,
    value: T,
) {
    SideEffect {
        println("📊 State change in $tag: $value")
    }
}

/**
 * Performance best practices checklist:
 *
 * 1. ✅ Use @Stable or @Immutable annotations on data classes
 * 2. ✅ Use remember with proper keys to cache expensive computations
 * 3. ✅ Use derivedStateOf for computed states
 * 4. ✅ Pass lambdas as references when possible (::function)
 * 5. ✅ Use LazyColumn/LazyRow for lists, not Column/Row with forEach
 * 6. ✅ Avoid creating objects in composable functions
 * 7. ✅ Use rememberUpdatedState for capturing changing values in effects
 * 8. ✅ Use key parameter in LazyColumn items for stable identity
 * 9. ✅ Use contentType parameter in LazyColumn for better reuse
 * 10. ✅ Minimize state reads in composables - read only what you need
 *
 * Common anti-patterns to avoid:
 *
 * 1. ❌ Using unstable collections as composable parameters
 * 2. ❌ Creating new lambdas on every recomposition
 * 3. ❌ Reading state in parent composables unnecessarily
 * 4. ❌ Using derivedStateOf without proper dependencies
 * 5. ❌ Forgetting to use remember for expensive calculations
 * 6. ❌ Using LazyColumn inside another LazyColumn
 * 7. ❌ Not providing stable keys to LazyColumn items
 * 8. ❌ Passing entire state objects when only one field is needed
 * 9. ❌ Using Box/Column/Row with many children instead of LazyColumn
 * 10. ❌ Not using Modifier.animateItemPlacement() for list animations
 */

/**
 * Extension to check if a class is stable for Compose
 */
inline fun <reified T> checkStability(): String {
    val className = T::class.simpleName ?: "Unknown"
    val annotations = T::class.annotations
    val hasStableAnnotation =
        annotations.any {
            it.annotationClass.simpleName?.contains("Stable") == true ||
                it.annotationClass.simpleName?.contains("Immutable") == true
        }

    return if (hasStableAnnotation) {
        "✅ $className is stable"
    } else {
        "⚠️ $className might not be stable - consider adding @Stable or @Immutable"
    }
}
