package code.name.monkey.retromusic.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.helper.MusicPlayerRemote
import code.name.monkey.retromusic.helper.menu.GenreMenuHelper
import code.name.monkey.retromusic.interfaces.CabHolder
import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.mvp.contract.GenreDetailsContract
import code.name.monkey.retromusic.mvp.presenter.GenreDetailsPresenter
import code.name.monkey.retromusic.ui.activities.base.AbsSlidingMusicPanelActivity
import code.name.monkey.retromusic.ui.adapter.song.SongAdapter
import code.name.monkey.retromusic.util.RetroColorUtil
import code.name.monkey.retromusic.util.ViewUtil
import com.afollestad.materialcab.MaterialCab
import kotlinx.android.synthetic.main.activity_playlist_detail.*
import java.util.*

/**
 * @author Hemanth S (h4h13).
 */

class GenreDetailsActivity : AbsSlidingMusicPanelActivity(), GenreDetailsContract.GenreDetailsView, CabHolder {

    private var genre: Genre? = null
    private var presenter: GenreDetailsPresenter? = null
    private var songAdapter: SongAdapter? = null
    private var cab: MaterialCab? = null

    private fun checkIsEmpty() {
        empty!!.visibility = if (songAdapter!!.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)


        setStatusbarColor(Color.TRANSPARENT)
        setNavigationbarColorAuto()
        setTaskDescriptionColorAuto()
        setLightNavigationBar(true)
        setLightStatusbar(ColorUtil.isColorLight(ThemeStore.primaryColor(this)))
        toggleBottomNavigationView(true)

        genre = intent.extras!!.getParcelable(EXTRA_GENRE_ID)
        presenter = GenreDetailsPresenter(this, genre!!.id)

        setUpToolBar()
        setupRecyclerView()
        actionShuffle.setOnClickListener { MusicPlayerRemote.openAndShuffleQueue(songAdapter!!.dataSet, true) }
    }

    private fun setUpToolBar() {
        bannerTitle!!.text = genre!!.name
        bannerTitle!!.setTextColor(ThemeStore.textColorPrimary(this))

        val primaryColor = ThemeStore.primaryColor(this)
        appBarLayout.setBackgroundColor(primaryColor)
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp)
            setBackgroundColor(primaryColor)
            setSupportActionBar(this)
            ToolbarContentTintHelper.colorBackButton(this, ThemeStore.textColorSecondary(this@GenreDetailsActivity))
        }
        actionShuffle.setColor(ThemeStore.accentColor(this@GenreDetailsActivity))
        title = null
    }

    override fun onResume() {
        super.onResume()
        presenter!!.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter!!.unsubscribe()
    }

    override fun createContentView(): View {
        return wrapSlidingMusicPanel(R.layout.activity_playlist_detail)
    }


    override fun loading() {

    }

    override fun showEmptyView() {

    }

    override fun completed() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_genre_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return GenreMenuHelper.handleMenuClick(this, genre!!, item)
    }

    private fun setupRecyclerView() {
        ViewUtil.setUpFastScrollRecyclerViewColor(this, recyclerView, ThemeStore.accentColor(this))
        songAdapter = SongAdapter(this, ArrayList(), R.layout.item_list, false, this)
        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@GenreDetailsActivity)
            adapter = songAdapter
        }.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    actionShuffle.setShowTitle(false)
                } else if (dy < 0) {
                    actionShuffle.setShowTitle(true)
                }
            }
        })
        songAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
    }

    override fun showData(list: ArrayList<Song>) {
        songAdapter!!.swapDataSet(list)
    }

    override fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab {
        if (cab != null && cab!!.isActive) cab!!.finish()
        cab = MaterialCab(this, R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(RetroColorUtil.shiftBackgroundColorForLightText(ThemeStore.primaryColor(this)))
                .start(callback)
        return cab!!
    }

    override fun onBackPressed() {
        if (cab != null && cab!!.isActive)
            cab!!.finish()
        else {
            recyclerView!!.stopScroll()
            super.onBackPressed()
        }
    }

    override fun onMediaStoreChanged() {
        super.onMediaStoreChanged()
        presenter!!.subscribe()
    }

    companion object {
        const val EXTRA_GENRE_ID = "extra_genre_id"
    }
}
