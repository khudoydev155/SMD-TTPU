package uz.ttpu.composearchlab

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class News(val title: String, val subtitle: String, val views: Int)

// IZOH 1 (qaysi Header yaxshiroq): Header(title, subtitle) yaxshiroq dizayn.
// U faqat ko'rsatishi kerak bo'lgan ikkita maydonni oladi, shuning uchun:
//  (a) News klassiga bog'liq emas - qayta ishlatish, preview va test qilish oson;
//  (b) News ichidagi aloqasiz maydon (views) o'zgarganda uning kirishlari (title, subtitle)
//      o'zgarmaydi va Compose uni qayta kompozitsiya qilmasdan o'tkazib yuboradi (skipped).
// Header(news) esa views o'zgarganda ham yangi News nusxasi kelgani uchun har safar qayta chiziladi.
@Composable
fun Header(news: News) {
    SideEffect { Log.d("Recompose", "Header(news)") }
    Column {
        Text(news.title)
        Text(news.subtitle)
    }
}

@Composable
fun Header(title: String, subtitle: String) {
    SideEffect { Log.d("Recompose", "Header(title, subtitle)") }
    Column {
        Text(title)
        Text(subtitle)
    }
}

// IZOH 2 (qachon parametrlarni bitta klassga jamlash kerak): parametrlar mantiqan BITTA
// tushunchani tashkil qilsa va doim birga o'zgarsa/uzatilsa (masalan, ArtistCard uchun Artist),
// hamda alohida parametrlar soni ko'payib, imzo o'qib bo'lmas holga kelsa. Shunda ham composable'ga
// ishlatadigan qismidan ortig'ini bermaymiz: butun domen obyektini emas, UI uchun mo'ljallangan
// kichik, o'zgarmas (immutable) UI-modelni beramiz.
@Composable
fun NewsScreen(modifier: Modifier = Modifier) {
    var news by remember {
        mutableStateOf(News("Compose is declarative", "State in, UI out", views = 0))
    }
    Column(modifier.padding(24.dp)) {
        Header(news)
        Header(news.title, news.subtitle)
        Text("Views: ${news.views}")
        Button(onClick = { news = news.copy(views = news.views + 1) }) {
            Text("Add view")
        }
    }
}
