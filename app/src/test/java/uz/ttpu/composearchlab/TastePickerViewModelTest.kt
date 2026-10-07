package uz.ttpu.composearchlab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.composearchlab.ui.taste.TastePickerViewModel

class TastePickerViewModelTest {

    @Test
    fun toggling_like_adds_then_removes_artist() {
        val vm = TastePickerViewModel()

        vm.onArtistLikeToggled(1)
        assertEquals(setOf(1), vm.state.value.likedIds)

        vm.onArtistLikeToggled(1)
        assertTrue(vm.state.value.likedIds.isEmpty())
    }

    @Test
    fun continue_is_enabled_only_after_three_likes() {
        val vm = TastePickerViewModel()

        vm.onArtistLikeToggled(1)
        vm.onArtistLikeToggled(2)
        assertFalse(vm.state.value.canContinue)

        vm.onArtistLikeToggled(3)
        assertTrue(vm.state.value.canContinue)
    }

    @Test
    fun selecting_genre_filters_and_tapping_again_clears() {
        val vm = TastePickerViewModel()

        vm.onGenreClick("Rock")
        val rock = vm.state.value.visibleArtists
        assertEquals(3, rock.size)
        assertTrue(rock.all { it.genre == "Rock" })

        vm.onGenreClick("Rock")
        assertEquals(12, vm.state.value.visibleArtists.size)
    }

    @Test
    fun likes_survive_changing_the_genre_filter() {
        val vm = TastePickerViewModel()

        vm.onArtistLikeToggled(1)
        vm.onGenreClick("Jazz")

        assertEquals(setOf(1), vm.state.value.likedIds)
    }
}
