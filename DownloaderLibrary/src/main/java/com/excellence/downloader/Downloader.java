package com.excellence.downloader;

import java.io.File;
import java.util.LinkedList;

import com.excellence.downloader.utils.IListener;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2017/8/9
 *     desc   : 下载器初始化
 *              权限
 *                  {@link android.Manifest.permission#INTERNET}
 *                  {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}
 *                  {@link android.Manifest.permission#READ_EXTERNAL_STORAGE}
 * </pre>
 */

public class Downloader
{
	public static final String TAG = Downloader.class.getSimpleName();

	public static final int DEFAULT_TASK_COUNT = 2;
	public static final int DEFAULT_THREAD_COUNT = 1;

	private static Downloader mInstace = null;
	private FileDownloader mFileDownloader = null;

	private Downloader()
	{

	}

	/**
	 * 初始化，默认任务数:2，单线程下载
	 *
	 * @param context
	 */
	public static void init(@NonNull Context context)
	{
		init(context, DEFAULT_TASK_COUNT, DEFAULT_THREAD_COUNT);
	}

	/**
	 * 初始化，设置任务数，单个任务下载线程数
	 *
	 * @param context     上下文
	 * @param parallelTaskCount   任务数
	 * @param threadCount 单个任务下载线程数
	 */
	public static void init(@NonNull Context context, @IntRange(from = 1) int parallelTaskCount, @IntRange(from = 1) int threadCount)
	{
		if (mInstace != null)
		{
			Log.w(TAG, "Downloader initialized!!!");
			return;
		}

		if (parallelTaskCount >= Runtime.getRuntime().availableProcessors())
		{
			Log.w(TAG, "ParallelTaskCount is beyond!!!");
			parallelTaskCount = Runtime.getRuntime().availableProcessors() == 1 ? 1 : Runtime.getRuntime().availableProcessors() - 1;
		}
		mInstace = new Downloader();
		mInstace.mFileDownloader = new FileDownloader(parallelTaskCount, threadCount);
	}

	/**
	 * 新建下载任务
	 *
	 * @param storeFile 文件
	 * @param url 下载链接
	 * @param listener
	 * @return 下载任务
	 */
	public static DownloadTask addTask(@NonNull File storeFile, @NonNull String url, IListener listener)
	{
		checkDownloader();
		return mInstace.mFileDownloader.addTask(storeFile, url, listener);
	}

	/**
	 * 新建下载任务
	 *
	 * @param filePath 文件路径
	 * @param url 下载链接
	 * @param listener
	 * @return 下载任务
	 */
	public static DownloadTask addTask(@NonNull String filePath, @NonNull String url, IListener listener)
	{
		return addTask(new File(filePath), url, listener);
	}

	public static DownloadTask get(File storeFile, String url)
	{
		checkDownloader();
		return mInstace.mFileDownloader.get(storeFile, url);
	}

	/**
	 * 获取下载队列
	 *
	 * @return
	 */
	public static LinkedList<DownloadTask> getTaskQueue()
	{
		checkDownloader();
		return mInstace.mFileDownloader.getTaskQueue();
	}

	private static void checkDownloader()
	{
		if (mInstace == null)
			throw new RuntimeException("Downloader not initialized!!!");
	}
}