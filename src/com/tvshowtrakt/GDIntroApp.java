package com.tvshowtrakt;

import greendroid.app.GDApplication;

/**
 * Classe necess‡ria para a ActionBar
 */
public class GDIntroApp extends GDApplication {

	/**
	 * Metodo que Ž chamado quando se carrega no bot‹o home da action bar
	 */
	@Override
	public Class<GalleryActivity> getHomeActivityClass() {
		return GalleryActivity.class;

	}

}
