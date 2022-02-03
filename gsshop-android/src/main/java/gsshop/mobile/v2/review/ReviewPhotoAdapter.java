/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.attach.FileAttachUtils;
import gsshop.mobile.v2.util.ImageUtil;

/**
 * 상품평 사진 리스트 어뎁터.
 *
 */
public class ReviewPhotoAdapter extends RecyclerView.Adapter<ReviewPhotoAdapter.PhotoViewHolder> {

	private final List<ReviewPhotoInfo> mPhotoInfos;
	private final Context mContext;

	public ReviewPhotoAdapter(Context context, List<ReviewPhotoInfo> photos) {
		mContext = context;
		mPhotoInfos = photos;
	}

	@Override
	public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View itemView = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.review_photo_item, viewGroup, false);

		return new PhotoViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(PhotoViewHolder holder, final int position) {
		ReviewPhotoInfo info = mPhotoInfos.get(position);
		holder.mTakePhotoButton.setVisibility(!info.added ? View.VISIBLE : View.GONE);
		holder.mPhotoView.setVisibility(info.added ? View.VISIBLE : View.GONE);

		if (info.added) {
			holder.mPhotoImage.setImageBitmap(null);
			if (info.photo.imageUrl != null) {
				//상품평 수정화면 진입시 서버에 기 등록되어 있던 이미지를 뿌려줄 경우
				//상품평 수정 API를 호출하면 imageUrl에 "http://...." 형태의 이미지 주소가 세팅됨
				ImageUtil.loadImage(mContext, info.photo.imageUrl, holder.mPhotoImage, android.R.color.transparent);
			} else {
				//갤러리 또는 카메라를 통해 이미지를 선택한 경우
				ImageUtil.loadImage(mContext, "file://" + info.photo.fullImageUri, holder.mPhotoImage, android.R.color.transparent);
			}

		}

		//사진촬영,앱범선택 팝업창 노출
		holder.mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int count = 0;
				// 사용자가 선택 가능한 사진 갯수를 계산
				for (ReviewPhotoInfo photo : mPhotoInfos) {
					if (!photo.added) {
						count++;
					}
				}

				FileAttachUtils.executePhotoPopup((Activity)mContext, count);
			}
		});

		//"X"버튼 클릭시 클릭한 위치의 객체 삭제 후 가장 뒤에 신규객체 생성
		holder.mDelePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPhotoInfos.remove(position);
				mPhotoInfos.add(new ReviewPhotoInfo());
				notifyDataSetChanged();
			}
		});

	}


	@Override
	public int getItemCount() {
		return mPhotoInfos == null ? 0 : mPhotoInfos.size();
	}

	/**
	 * 뷰홀더
	 *
	 */
	public static class PhotoViewHolder extends RecyclerView.ViewHolder {

		public LinearLayout layPhoto;
		public ImageButton mTakePhotoButton;
		public ImageButton mDelePhotoButton;
		public ImageView mPhotoImage;
		public View mPhotoView;

		public PhotoViewHolder(View itemView) {
			super(itemView);
			layPhoto = (LinearLayout) itemView.findViewById(R.id.lay_photo);
			mPhotoImage = (ImageView) itemView.findViewById(R.id.img_photo);
			mTakePhotoButton = (ImageButton) itemView.findViewById(R.id.btn_take_photo);
			mDelePhotoButton = (ImageButton) itemView.findViewById(R.id.btn_delete_photo);
			mPhotoView = itemView.findViewById(R.id.view_photo);

		}
	}
}