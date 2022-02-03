package gsshop.mobile.v2.attach;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.ImageUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GalleryPhotoListPageFragment extends GalleryBaseFragment {

	// 선택한 이미지 리스트
	private List<PhotoItem> mSelectedPhotoes = null;
	private GridView mList;
	private TextView mTitleText;
	private ImageView mBackButton;
	private Button mAddButton;
	private String type;
	private boolean isImageSearch;
	private boolean isEventVideo = false;
	private int maxVideoSize = 0;

	/**
	 * 프래그먼트에서 사용할 액티비티
	 */
	private Activity mActivity;

	/**
	 * 첨부이미지 최대 용량 (10M)
	 * 제한 해제
	 */
	//private final static long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

	/**
	 * 첨부동영상 최대 용량 디폴트값 (200MB)
	 */
	private final static long DEFAULT_MAX_VIDEO_SIZE = 200;

	/**
	 * 이벤트비디오 타입 정의
	 */
	private final static String EVENT_VIDEO_TYPE = "eventvideo";

	/**
	 * 선택가능한 사진갯수
	 */
	private int maxCount;
	
	/**
	 * 선택이미지 해제시 깜빡거림을 방지하기 위해 잠시 ImageView lock을 걸기위해 사용
	 */
	private boolean reloadLock;

	/**
	 * onCreateView
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
     * @return View
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_page_gallery_image_grid, container, false);

		/* hide gnb */
		mTitleText = (TextView) rootView.findViewById(R.id.text_title);
		mBackButton = (ImageView) rootView.findViewById(R.id.button_back);
		mAddButton = (Button) rootView.findViewById(R.id.button_add);
		rootView.findViewById(R.id.view_down).setVisibility(View.GONE);
		rootView.findViewById(R.id.button_catalog).setVisibility(View.GONE);
		rootView.findViewById(R.id.button_search).setVisibility(View.GONE);

		mList = (GridView) rootView.findViewById(R.id.grid);

//		maxCount = 9 - MainApplication.uploadCount;
		try{
			maxCount = Integer.parseInt(MainApplication.fileAttachParamInfo.getImageCount());
		}catch(NumberFormatException e){
			maxCount = 1;
		}
		type = mActivity.getIntent().getStringExtra("type");
		isImageSearch = mActivity.getIntent().getBooleanExtra("isImageSearch", false);
		
		if("video".equals(type)){
			mAddButton.setVisibility(View.GONE);
		}else{
			mAddButton.setVisibility(View.VISIBLE);
		}

		//동영상 최대용량 세팅
		maxVideoSize = mActivity.getIntent().getIntExtra("maxVideoSize", (int)DEFAULT_MAX_VIDEO_SIZE);
		maxVideoSize = maxVideoSize * 1024 * 1024;

		//동영상 종류가 이벤트용이면 스트림방식 전송, 진행률 프로그레스바 표시
		isEventVideo = mActivity.getIntent().getBooleanExtra("isEventVideo", false);

		//MSLEE 09/19
		//이미지 첨부가 한개만 요청되어도 선택버튼은 사라지지 않는다,사용자가 실수록 선택시 바로 업로드 되기 때문에 CS 가 들어옴
		//카운트가 하나 들어와도 "선택 버튼을 없애기 않는다.
		/*
		if(maxCount == 1){
			mAddButton.setVisibility(View.GONE);
		}else{
			mAddButton.setVisibility(View.VISIBLE);
		}
		*/
		mAddButton.setVisibility(View.VISIBLE);

		mAddButton.setText(R.string.gallery_button_add_text);
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// NOTE Auto-generated method stub
				EventBus.getDefault().post(new Events.GalleryEvent.GoBackEvent());
			}
		});

		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// NOTE Auto-generated method stub
				if (mSelectedPhotoes != null && mSelectedPhotoes.isEmpty()) {
					new CustomOneButtonDialog(getActivity()).message(R.string.article_photo_validation).buttonClick(CustomOneButtonDialog.DISMISS).show();
					return;

				}

				if (isImageSearch) {
					Bitmap bitmapTmp = mSelectedPhotoes.get(0).getThumbnail(getContext());
					if (Math.max(bitmapTmp.getHeight(), bitmapTmp.getWidth()) >
							(Math.min(bitmapTmp.getHeight(), bitmapTmp.getWidth()) * 3)) {
						new CustomOneButtonDialog(getActivity()).message(R.string.camera_failed_size_is_to_big).buttonClick(CustomOneButtonDialog.DISMISS).show();
						return;
					}
				}

				MainApplication.articlePhotoes = mSelectedPhotoes;

				Intent intent = new Intent();
				if (isEventVideo) {
					intent.putExtra("type", EVENT_VIDEO_TYPE);
				} else {
					intent.putExtra("type", type);
				}

				intent.putExtra("isImageSearch", isImageSearch);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();

			}
		});



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
	 * @param obj
     */
	@Override
	public void initUI(Object obj) {
		// NOTE Auto-generated method stub
		@SuppressWarnings("unchecked")
		List<PhotoItem> items = (List<PhotoItem>) obj;
		//Ln.i("getActivity : " + getActivity());
		mList.setAdapter(new MyListAdapter(mActivity, items));

		if(items != null && !items.isEmpty()) {
			mTitleText.setText(items.get(0).pathName);
		}
		mSelectedPhotoes = new ArrayList<PhotoItem>();
	}

	/**
	 * MyListAdapter
	 *
	 */
	public class MyListAdapter extends ArrayAdapter<PhotoItem> {

		/**
		 * RESOURCE_ID
		 */
		private static final int RESOURCE_ID = R.layout.fragment_item_gallery_photo_list;

		/**
		 * MyListAdapter
		 *
		 * @param context
		 * @param objects List
         */
		public MyListAdapter(Context context, List<PhotoItem> objects) {
			super(context, RESOURCE_ID, objects);
			// TODO Auto-generated constructor stub
		}

		/**
		 * getView
		 *
		 * @param position
		 * @param convertView
		 * @param parent
         * @return View
         */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(RESOURCE_ID, parent, false);
				holder.selectView = v.findViewById(R.id.view_select);
				holder.thumbImage = (ImageView) v.findViewById(R.id.image_thumb);
				holder.borderImage = (ImageView) v.findViewById(R.id.image_border);
				holder.numberText = (TextView) v.findViewById(R.id.text_number);
				
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			final PhotoItem item = getItem(position);

			final ImageView image_border = holder.borderImage;
			final TextView text_number = holder.numberText;

			image_border.setVisibility(View.GONE);
			text_number.setVisibility(View.GONE);

			int i = 0;
			for (PhotoItem p : mSelectedPhotoes) {
				if (p.fullImageUri.equals(item.fullImageUri)) {
					image_border.setVisibility(View.VISIBLE);
					text_number.setVisibility(View.VISIBLE);
					text_number.setText(Integer.toString(i + 1));
					break;
				}
				i++;
			}

			if("video".equals(type)){
				holder.thumbImage.setImageBitmap(mGetVideoThumnailImg(item.thumbnailImageID));
			}else{
				if(!reloadLock){
					ImageUtil.loadImage(getActivity(), "file://" + item.fullImageUri, holder.thumbImage, android.R.color.transparent);
				}
			}


			holder.selectView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {


					int j = 0;
 					for (PhotoItem p : mSelectedPhotoes) {
						if (p.fullImageUri.equals(item.fullImageUri)) {
							break;
						}
						j++;
					}

					if (j >= mSelectedPhotoes.size()) {

						
						if(mSelectedPhotoes.size() >= maxCount){
							new CustomOneButtonDialog(getActivity()).message(getContext().getString(R.string.article_photo_validation_min_length)).buttonClick(CustomOneButtonDialog.DISMISS).show();
							return;
						}

						if("video".equals(type) && !mSelectedPhotoes.isEmpty()){
							new CustomOneButtonDialog(getActivity()).message(R.string.article_video_validation).buttonClick(CustomOneButtonDialog.DISMISS).show();
							return;
						}

						File f = new File(item.fullImageUri.toString());

						//20171103 이미지 제한 해제 이주현 대리님
						//if(!"video".equals(type) && f.length() > MAX_IMAGE_SIZE){
						//	new CustomOneButtonDialog(getActivity()).message(R.string.article_photo_size_over).buttonClick(CustomOneButtonDialog.DISMISS).show();
						//	return;
						//}
						
						String fileName = f.getName();
						fileName = fileName.substring(fileName.lastIndexOf('.')+1,fileName.length());

						//이미지는 PNG, JPG만 업로드 가능하도록 처리
						if("image".equals(type) && !"jpg".equalsIgnoreCase(fileName) && !"png".equalsIgnoreCase(fileName)
								&& !"jpeg".equalsIgnoreCase(fileName)){
							new CustomOneButtonDialog(getActivity()).message(R.string.article_photo_file_format).buttonClick(CustomOneButtonDialog.DISMISS).show();
							return;
						}

						if("video".equals(type) && !"mp4".equalsIgnoreCase(fileName)){
							new CustomOneButtonDialog(getActivity()).message(R.string.article_video_file_format).buttonClick(CustomOneButtonDialog.DISMISS).show();
							return;
						}
						
						if("video".equals(type) && f.length() > maxVideoSize){
							new CustomOneButtonDialog(getActivity()).message(getContext().getString(R.string.article_video_size_over, (maxVideoSize/1024)/1024))
									.buttonClick(CustomOneButtonDialog.DISMISS).show();
							return;
						}
						

						mSelectedPhotoes.add(item);
						image_border.setVisibility(View.VISIBLE);
						text_number.setVisibility(View.VISIBLE);
						text_number.setText(Integer.toString(j + 1));

						if("video".equals(type)){
							MainApplication.articlePhotoes = mSelectedPhotoes;

							Intent intent = new Intent();
							if (isEventVideo) {
								intent.putExtra("type", EVENT_VIDEO_TYPE);
							} else {
								intent.putExtra("type", type);
							}
							intent.putExtra("isImageSearch", isImageSearch);
							mActivity.setResult(Activity.RESULT_OK, intent);
							mActivity.finish();
						}

						/*
						// 09/19 이미지가 하나만 선택되어도 바로 선택된 이미지를 택하지 않도록 처리
						if(type.equals("image") && maxCount == 1){
							MainApplication.articlePhotoes = mSelectedPhotoes;

							Intent intent = new Intent();
							intent.putExtra("type", type);

							mActivity.setResult(Activity.RESULT_OK, intent);
							mActivity.finish();
						}
						*/

					} else {
						mSelectedPhotoes.remove(j);
						image_border.setVisibility(View.GONE);
						text_number.setVisibility(View.GONE);
						reloadLock = true;
						notifyDataSetChanged();
						
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								reloadLock = false;
							}
						}, 500);
					}

				}
			});

			return v;
		}

		/**
		 * ViewHolder
		 */
		class ViewHolder {
			ImageView thumbImage;
			ImageView borderImage;
			TextView numberText;
			View selectView;
		}

	}

	/**
	 * 비디오 썸네일 이미지 가져오기
	 * @param id
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
	 * ImageDownloaderTask
	 */
	class ImageDownloaderTask extends AsyncTask<PhotoItem, Void, Bitmap> {

		/**
		 * WeakReference
		 */
		private final WeakReference imageViewReference;

		/**
		 * ImageDownloaderTask
		 *
		 * @param imageView
         */
		public ImageDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference(imageView);
		}

		/**
		 * doInBackground
		 * NOTE : Actual download method, run in the task thread
		 * @param params
         * @return
         */
		@Override
		protected Bitmap doInBackground(PhotoItem... params) {

			if (params[0].thumbnail == null) {
				params[0].thumbnail = MediaStore.Images.Thumbnails.getThumbnail(mActivity.getContentResolver(), params[0].thumbnailImageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
			}

			return params[0].thumbnail;
		}

		/**
		 * onPostExecute
		 * NOTE : Once the image is downloaded, associates it to the imageView
		 * @param bitmap
         */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			//			if (isCancelled()) {
			//				bitmap = null;
			//			}
			//			if (imageViewReference != null) {
			//				ImageView imageView = (ImageView) imageViewReference.get();
			//				if (imageView != null) {
			//
			//					if (bitmap != null) {
			//						imageView.setImageBitmap(bitmap);
			//					} else {
			//						imageView.setImageDrawable(imageView.getContext().getResources()
			//								.getDrawable(R.drawable.ab_bottom_solid_google_plus));
			//					}
			//				}
			//
			//			}
		}

	}

}
