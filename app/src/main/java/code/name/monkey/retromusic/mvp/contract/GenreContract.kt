/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.mvp.contract

import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.mvp.BasePresenter
import code.name.monkey.retromusic.mvp.BaseView

import java.util.ArrayList

/**
 * @author Hemanth S (h4h13).
 */

interface GenreContract {
    interface GenreView : BaseView<ArrayList<Genre>>

    interface Presenter : BasePresenter<GenreView> {
        fun loadGenre()
    }
}
