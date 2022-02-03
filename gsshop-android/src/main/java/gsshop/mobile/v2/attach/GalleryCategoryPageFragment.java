package gsshop.mobile.v2.attach;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.attach.gallery.LocalAlbum;
import gsshop.mobile.v2.attach.gallery.LocalMediaItem;
import gsshop.mobile.v2.util.ImageUtil;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * A simple {@link Fragment} subclass.
 * GalleryCategoryPageFragment
 *
 */
public class GalleryCategoryPageFragment extends GalleryBaseFragment {

	/**
	 * 프래그먼트에서 사용할 액티비티
	 */
	private Activity mActivity;

	/**
	 * mList
	 */
	private ListView mList;

	/**
	 * mTitleView
	 */
	private TextView mTitleView;

	/**
	 * mBackButton
	 */
	private ImageView mBackButton;

	/**
	 * mCategoryItems
	 */
	public List<CategoryItem> mCategoryItems = new ArrayList<CategoryItem>();

	/**
	 * mPhotoItems
	 */
	public List<PhotoItem> mPhotoItems;

	/**
	 * type
	 */
	private String type;

	/**
	 * onCreateView
	 *
	 * @param inflater inflater
	 * @param container container
	 * @param savedInstanceState savedInstanceState
     * @return View View
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_page_gallery_category, container, false);

		/* hide gnb */
		mTitleView = (TextView) rootView.findViewById(R.id.text_title);
		mBackButton = (ImageView) rootView.findViewById(R.id.button_back);
		rootView.findViewById(R.id.view_down).setVisibility(View.GONE);
		rootView.findViewById(R.id.button_add).setVisibility(View.GONE);
		rootView.findViewById(R.id.button_catalog).setVisibility(View.GONE);
		rootView.findViewById(R.id.button_search).setVisibility(View.GONE);

		mList = (ListView) rootView.findViewById(R.id.list);


		mTitleView.setText(R.string.gallery_title);
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// NOTE Auto-generated method stub
				EventBus.getDefault().post(new Events.GalleryEvent.GoBackEvent());
			}
		});

		type = mActivity.getIntent().getStringExtra("type");

		initUI(null);

		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof Activity) {
			mActivity = (Activity) context;
		}
	}

	/**
	 * goBack
	 *
	 * @return boolean
     */
	@Override
	public boolean goBack() {
		// NOTE Auto-generated method stub
		return false;
	}

	/**
	 * initUI
	 *
	 * @param obj LoadImageFromGallery
     */
	@Override
	public void initUI(Object obj) {
		new LoadImageFromGallery(getActivity()).execute();
	}

	/**
	 * LoadImageFromGallery
	 */
	public class LoadImageFromGallery extends BaseAsyncController<List<PhotoItem>> {

		/**
		 * LoadImageFromGallery
		 *
		 * @param activityContext activityContext
         */
		protected LoadImageFromGallery(Context activityContext) {
			super(activityContext);
			// NOTE Auto-generated constructor stub
		}

		/**
		 * onPrepare
		 *
		 * @param params 이미지
		 * @throws Exception 예외
         */
		@Override
		protected void onPrepare(Object... params) throws Exception {
			// NOTE Auto-generated method stub
			super.onPrepare(params);
			this.dialog.setCancelable(false);
		}

		/**
		 * process
		 *
		 * @return List<PhotoItem>  포토 리스트
		 * @throws Exception 예외
         */
		@Override
		protected List<PhotoItem> process() throws Exception {
			// NOTE Auto-generated method stub
			if("video".equals(type)){
				mPhotoItems = getVideoThumbnails(mActivity.getApplication());
			}else{
				mPhotoItems = getAlbumThumbnails(mActivity.getApplication());
			}


			// NOTE Auto-generated method stub
			for (PhotoItem pItem : mPhotoItems) {
				// first item
				if (mCategoryItems.isEmpty()) {
					CategoryItem item = new CategoryItem(pItem.getThumbnail(getActivity()), pItem.fullImageUri, pItem.pathName, 1);
					item.thumbnailImageID = pItem.thumbnailImageID;
					mCategoryItems.add(item);
					continue;
				}

				int i = 0;
				for (CategoryItem cItem : mCategoryItems) {
					if (cItem.pathName.equals(pItem.pathName)) {
						cItem.count = cItem.count + 1;
						break;
					}
					i++;
				}

				// add new category item
				if (i == mCategoryItems.size()) {
					CategoryItem item = new CategoryItem(pItem.getThumbnail(getActivity()), pItem.fullImageUri, pItem.pathName, 1);
					item.thumbnailImageID = pItem.thumbnailImageID;

					mCategoryItems.add(item);
				}

			}

			return mPhotoItems;

		}

		/**
		 * onSuccess
		 *
		 * @param photoItems photoItems
		 * @throws Exception Exception
         */
		@Override
		protected void onSuccess(List<PhotoItem> photoItems) throws Exception {


			mList.setAdapter(new MyListAdapter(this.context, mCategoryItems));
		}

		/**
		 * Fetch both full sized images and thumbnails via a single query.
		 * Returns all images not in the Camera Roll.
		 * 
		 * @param application application
		 * @return List
		 */
		public List<PhotoItem> getAlbumThumbnails(Application application) {

			LocalAlbum album = new LocalAlbum(application, true);

			List<LocalMediaItem> items = album.getMediaItem();
			List<PhotoItem> result = new ArrayList<PhotoItem>();
			if(items != null && !items.isEmpty()) {
				for (LocalMediaItem item : items) {
				    if (isNotEmpty(item.bucketDisplayName)) {
                        Uri fullImageUri = Uri.parse(item.filePath);
                        PhotoItem newItem = new PhotoItem(item.id, fullImageUri, item.bucketDisplayName);
                        result.add(newItem);
                    }
				}
			}
			return result;
		}
	}

	/**
	 * getVideoThumbnails
	 *
	 * @param application application
	 * @return List
     */
	public List<PhotoItem> getVideoThumbnails(Application application) {

		LocalAlbum album = new LocalAlbum(application, false);
		List<LocalMediaItem> items = album.getMediaItem();
		List<PhotoItem> result = new ArrayList<PhotoItem>();
		if(items != null && !items.isEmpty()) {
			for (LocalMediaItem item : items) {
                if (isNotEmpty(item.bucketDisplayName)) {
                    Uri fullImageUri = Uri.parse(item.filePath);
                    PhotoItem newItem = new PhotoItem(item.id, fullImageUri, item.bucketDisplayName);
                    result.add(newItem);
                }
			}
		}

		return result;
	}

	/**
	 * 비디오 썸네일 이미지 가져오기
	 *
	 * @param id 비디오 아이디
	 * @return Bitmap
     */
	public Bitmap mGetVideoThumnailImg(long id)
	{

		ContentResolver mCrThumb = mActivity.getContentResolver();
		BitmapFactory.Options options =	new BitmapFactory.Options();
		options.inSampleSize = 1;

		//MICRO_KIND :작은이미지(정사각형) MINI_KIND (중간이미지)
		return MediaStore.Video.Thumbnails.getThumbnail(mCrThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);

	}

	/**
	 * MyListAdapter
	 *
	 */
	public  class MyListAdapter extends ArrayAdapter<CategoryItem> {

		/**
		 * RESOURCE_ID
		 */
		private static final int RESOURCE_ID = R.layout.fragment_item_gallery_category;

		/**
		 * MyListAdapter
		 *
		 * @param context context
		 * @param objects List
         */
		public MyListAdapter(Context context, List<CategoryItem> objects) {
			super(context, RESOURCE_ID, objects);
			// NOTE Auto-generated constructor stub
		}

		/**
		 * getView
		 *
		 * @param position position
		 * @param convertView convertView
		 * @param parent parent
         * @return View View
         */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(RESOURCE_ID, parent, false);
				holder.thumbImage = (ImageView) v.findViewById(R.id.image_thumb);
				holder.titleText = (TextView) v.findViewById(R.id.text_title);
				holder.countText = (TextView) v.findViewById(R.id.text_count);
				holder.goView = v.findViewById(R.id.view_go);
				holder.is_video = (ImageView) v.findViewById(R.id.is_video);
				v.setTag(holder);
				
				
			} else {
				holder = (ViewHolder) v.getTag();
			}

			final CategoryItem item = getItem(position);

			if("video".equals(type)){
				holder.thumbImage.setImageBitmap(mGetVideoThumnailImg(item.thumbnailImageID));
				holder.is_video.setVisibility(View.VISIBLE);
			}else{
				ImageUtil.loadImage(getActivity(), "file://" + item.fullImageUri, holder.thumbImage, android.R.color.transparent);
			}


			// title
			holder.titleText.setText(item.pathName);

			// count
			holder.countText.setText(new DecimalFormat("###,###,###").format(item.count));

			holder.goView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// NOTE Auto-generated method stub

					List<PhotoItem> items = new ArrayList<PhotoItem>();
					CategoryItem cItem = mCategoryItems.get(position);
					for(PhotoItem item : mPhotoItems) {
						if(cItem.pathName.equals(item.pathName)) {
							items.add(item);
						}
					}


					EventBus.getDefault().post(new Events.GalleryEvent.LoadImageGridEvent(items));

				}
			});
			return v;
		}

		/**
		 * ViewHolder
		 */
		class ViewHolder {
			/**
			 * is_video
			 */
			public ImageView is_video;
			/**
			 * thumbImage
			 */
			ImageView thumbImage;
			/**
			 * titleText
			 */
			TextView titleText;
			/**
			 * countText
			 */
			TextView countText;
			/**
			 * goView
			 */
			View goView;
		}
	}
}
