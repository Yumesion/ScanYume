package eu.kanade.presentation.category

import android.content.Context
import androidx.compose.runtime.Composable
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.domain.category.model.Category
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

val Category.visualName: String
    @Composable
    get() = when (id) {
        Category.FAVORITES_ID -> stringResource(MR.strings.label_favorites)
        Category.UNCATEGORIZED_ID -> stringResource(MR.strings.label_library)
        else -> name
    }

fun Category.visualName(context: Context): String =
    when (id) {
        Category.FAVORITES_ID -> context.stringResource(MR.strings.label_favorites)
        Category.UNCATEGORIZED_ID -> context.stringResource(MR.strings.label_library)
        else -> name
    }
