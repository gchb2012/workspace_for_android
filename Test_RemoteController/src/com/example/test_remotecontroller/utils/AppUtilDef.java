package com.example.test_remotecontroller.utils;


public class AppUtilDef {
	public static final int MSG_ID_AMBA_START_SESSION 					= 0x00000101; //257
	public static final int MSG_ID_AMBA_STOP_SESSION 					= 0x00000102; //258
	public static final int MSG_ID_AMBA_RESET_FV 						= 0x00000103; //259 reset the Remote View Finder(Set Setting is disable)
	public static final int MSG_ID_AMBA_STOP_FV 						= 0x00000104; //260 stop the Remote View Finder(Set Setting is enable)
	public static final int MSG_ID_AMBA_GET_SETTING 					= 0x00000001; //1
	public static final int MSG_ID_AMBA_SET_SETTING 					= 0x00000002; //2
	public static final int MSG_ID_AMBA_GET_ALL_CURRENT_SETTINGS 		= 0x00000003; //3
	public static final int MSG_ID_AMBA_GET_SPACE 						= 0x00000005; //5
	public static final int MSG_ID_AMBA_GET_FILES_NUM 					= 0x00000006; //6
	public static final int MSG_ID_AMBA_NOTIFICATION			 		= 0x00000007; //7
	public static final int MSG_ID_AMBA_GET_SINGLE_SETTING_OPTIONS 		= 0x00000009; //9
	public static final int MSG_ID_AMBA_RECORD_START 					= 0x00000201; //513
	public static final int MSG_ID_AMBA_RECORD_STOP 					= 0x00000202; //514
	public static final int MSG_ID_AMBA_GET_RECORD_TIME 				= 0x00000203; //515
	public static final int MSG_ID_AMBA_TAKE_PHOTO 						= 0x00000301; //769
	
	public static final int MSG_ID_AMBA_GET_THUMB 						= 0x00000401; //1025
	public static final int MSG_ID_AMBA_GET_MEDIAINFO 					= 0x00000402; //1026
	
	public static final int MSG_ID_AMBA_DEL_FILE 						= 0x00000501; //1281
	public static final int MSG_ID_AMBA_LS 								= 0x00000502; //1282
	public static final int MSG_ID_AMBA_CD 								= 0x00000503; //1283
	public static final int MSG_ID_AMBA_PWD 							= 0x00000504; //1284
	public static final int MSG_ID_AMBA_GET_FILE 						= 0x00000505; //1285
	public static final int MSG_ID_AMBA_PUT_FILE 						= 0x00000506; //1286
	
	public static final String KEY_MSG_ID								= "msg_id";
	public static final String KEY_TOKEN								= "token";
	public static final String KEY_RVAL									= "rval";
	public static final String KEY_PARAM								= "param";
	public static final String KEY_TYPE									= "type";
	public static final String KEY_PERMISSION							= "permission";
	public static final String KEY_OPTIONS								= "options";
	public static final String KEY_PWD									= "pwd";
	public static final String KEY_LISTING								= "listing";
	
	public static final String KEY_OFFSET								= "offset";
	public static final String KEY_FETCH_SIZE							= "fetch_size";
	
	public static final String KEY_FILE_THUMB							= "thumb_file";
	public static final String KEY_FILE_SIZE							= "size";
	public static final String KEY_FILE_DATE							= "date";
	public static final String KEY_FILE_RESOLUTION						= "resolution";
	public static final String KEY_FILE_DURATION						= "duration";
	public static final String KEY_FILE_MEDIA_TYPE						= "media_type";
	
	public static final String KEY_DOWNLOAD_REM_SIZE					= "rem_size";
	public static final String KEY_DOWNLOAD_TOTAL_SIZE					= "size";
	public static final String KEY_DOWNLOAD_MD5SUM						= "md5sum";
	public static final String KEY_DOWNLOAD_THUMB_FILE_NAME				= "thumb_file";
	
	public static final String TYPE_STREAM_TYPE 						= "stream_type";
	public static final String TYPE_APP_STATUS 							= "app_status";
	public static final String TYPE_STD_DEF_VIDEO 						= "std_def_video";
	public static final String TYPE_STREAM_WHILE_RECORD					= "stream_while_record";
	public static final String TYPE_VIDEO_RESOLUTION 					= "video_resolution";
	public static final String TYPE_VIDEO_QUALITY 						= "video_quality";
	public static final String TYPE_PHOTO_SIZE 							= "photo_size";
	public static final String TYPE_PHOTO_QUALITY 						= "photo_quality";
	public static final String TYPE_THUMB 								= "thumb";
	
	public static final String PARAM_STREAM_TYPE_RTSP 					= "rtsp";
	public static final String PARAM_STREAM_TYPE_MJPG 					= "mjpg";
	public static final String PARAM_STREAM_TYPE_HLS 					= "hls";
	public static final String PARAM_APP_STATUS_IDLE 					= "idle";   //Stop View Finder Mode
	public static final String PARAM_APP_STATUS_VF 						= "vf";     //View Finder Mode
	public static final String PARAM_APP_STATUS_RECORD 					= "record";
	public static final String PARAM_APP_STATUS_CAPTURE					= "capture";
	
	public static final String NOTIFICATION_TYPE_SNAP2_PRESSED			= "SNAP2_pressed";
	public static final String NOTIFICATION_TYPE_MENU_PRESSED			= "MENU_pressed";
	public static final String NOTIFICATION_TYPE_SET_PRESSED			= "SET_pressed";
	public static final String NOTIFICATION_TYPE_DISCONNECT_STORAGE_IO_ERROR = "disconnect_STORAGE_IO_ERROR";
	
	public static final String NOTIFICATION_GET_FILE_COMPLETE			= "get_file_complete";
	public static final String NOTIFICATION_GET_FILE_TIMEOUT			= "get_file_timeout";
	public static final String NOTIFICATION_PUT_FILE_COMPLETE			= "put_file_complete";
	public static final String NOTIFICATION_DISCONNECT_HDMI				= "disconnect_HDMI";
	public static final String NOTIFICATION_DISCONNECT_SHUTDOWN			= "disconnect_shutdown";
	public static final String NOTIFICATION_STARTING_VIDEO_RECORD		= "starting_video_record";
	public static final String NOTIFICATION_VIDEO_RECORD_COMPLETE		= "video_record_complete";
	public static final String NOTIFICATION_PHOTO_TAKEN					= "photo_taken";
	public static final String NOTIFICATION_LOW_BATTERY_WARNING			= "low_battery_warning";
	public static final String NOTIFICATION_LOW_STORAGE_WARNING			= "low_storage_warning";
	public static final String NOTIFICATION_TIMELAPSE_PHOTO_INTERVAL_CHANGE	= "timelapse_photo_interval_change";
	public static final String NOTIFICATION_TIMELAPSE_PHOTO_STATUS		= "timelapse_photo_status";
	
	public static final int SYSTEM_RETURN_SUCCESSFUL					= 0;
	public static final int SYSTEM_ERRORS_UNKNOWN_ERROR					= -1;
	public static final int SYSTEM_ERRORS_SESSION_START_FAIL			= -3;
	public static final int SYSTEM_ERRORS_CAMERA_MANUAL_OVERRIDE		= -5;
	public static final int SYSTEM_ERRORS_JSON_PACKAGE_ERROR			= -7;
	public static final int SYSTEM_ERRORS_JSON_PACKAGE_TIMEOUT			= -8;
	public static final int SYSTEM_ERRORS_JSON_SYNTAX_ERROR				= -9;
	public static final int SYSTEM_ERRORS_INVALID_OPTION_VALUE			= -13;
	public static final int SYSTEM_ERRORS_INVALID_OPERATION				= -14;
	public static final int SYSTEM_ERRORS_HDMI_INSERTED					= -16;
	public static final int SYSTEM_ERRORS_NO_MORE_SPACE					= -17;
	public static final int SYSTEM_ERRORS_CARD_PROTECTED				= -18;
	public static final int SYSTEM_NO_MORE_MEMORY						= -19;
}
