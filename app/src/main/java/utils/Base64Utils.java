package utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.example.administrator.myapplication.BluetoothChat.config.CacheConfig;
import com.example.administrator.myapplication.BluetoothChat.model.BluChatMsgBean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/27.
 */
public class Base64Utils {
    /**
     * encodeBase64File:(将文件转成base64 字符串)
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }


    /**
     * 根据byte数组转换为base64编码字符
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    public static String encodeBase64Byte(byte[] buffer) throws Exception {
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件)
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static void decoderBase64File(String base64Code, String savePath) throws Exception {
        TLogUtils.d("lyn_savePath", savePath);
        //byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }


    /**
     * decoderBase64File:(将base64字符二次解码保存为视频文件)
     */
    public static void decoderBase64VideoFile(Context mContext, BluChatMsgBean message, String savePath, Handler handler) throws Exception {

        String base64Code = message.getContent();

        TLogUtils.d("lyn_savePath", savePath);

        byte[] buffer1 = Base64.decode(base64Code, Base64.DEFAULT);

        // 获取文件总长度
        int totalLength = buffer1.length;

        String cacheFile = FileUtil.getDiskFileDir(mContext, CacheConfig.VIDEO_BLU) + "/" + new Date() + "yiJICache.mp4";
        //一次解码,解码Base64串
        FileOutputStream out = new FileOutputStream(cacheFile);
        out.write(buffer1);
        out.close();

        FileInputStream fis = new FileInputStream(cacheFile);
        RandomAccessFile raf = new RandomAccessFile(savePath, "rw");
        BufferedInputStream bis = new BufferedInputStream(fis);
        int readSize = 0;

        int mdatSize = 0;// mp4的mdat长度
        int headSize = 0;// mp4头长度
        byte[] boxSizeBuf = new byte[4];
        byte[] boxTypeBuf = new byte[4];
        // 由MP4的文件格式读取
        int boxSize = readBoxSize(bis, boxSizeBuf);
        String boxType = readBoxType(bis, boxTypeBuf);
        raf.write(boxSizeBuf);
        raf.write(boxTypeBuf);

        while (!boxType.equalsIgnoreCase("moov")) {
            int count = boxSize - 8;
            if (boxType.equalsIgnoreCase("ftyp")) {
                headSize += boxSize;
                byte[] ftyps = new byte[count];
                bis.read(ftyps, 0, count);
                raf.write(ftyps, 0, count);
            } else if (boxType.equalsIgnoreCase("mdat")) {
                // 标记mdat数据流位置，在后面reset时读取
                bis.mark(totalLength - headSize);
                // 跳过mdat数据
                skip(bis, count);
                mdatSize = count;
                byte[] mdatBuf = new byte[mdatSize];
                raf.write(mdatBuf);
            } else if (boxType.equalsIgnoreCase("free")) {
                headSize += boxSize;
            }

            boxSize = readBoxSize(bis, boxSizeBuf);
            boxType = readBoxType(bis, boxTypeBuf);
            raf.write(boxSizeBuf);
            raf.write(boxTypeBuf);
        }

        // 读取moov数据
        byte[] buffer = new byte[4096];
        int moovSize = 0;
        while ((readSize = bis.read(buffer)) != -1) {
            moovSize += readSize;
            raf.write(buffer, 0, readSize);
        }

        // 返回到mdat数据开始
        bis.reset();
        // 设置文件指针偏移到mdat位置
        long offset = raf.getFilePointer() - moovSize - mdatSize - 8;
        raf.seek(offset);

        // 读取mdat数据，设置mp4初始mdat的缓存大小
        int buf_size = 56 * 1024;// 56kb
        int downloadCount = 0;
        boolean viable = false;
        while (mdatSize > 0) {
            readSize = bis.read(buffer);
            raf.write(buffer, 0, readSize);
            mdatSize -= readSize;
            downloadCount += readSize;
            if (handler != null && !viable && downloadCount >= buf_size) {
                viable = true;
                //可以播放了，但是未处理完毕
            }
        }
        // 发送视频处理完毕消息
        if (handler != null) {
            message.setFilePath(savePath);
            Message msg = new Message();
            msg.what = 2;
            msg.obj = savePath;
            handler.sendMessage(msg);
        }
        bis.close();
        fis.close();
        raf.close();
    }

    /**
     * 跳转
     *
     * @param is
     * @param count 跳转长度
     * @throws IOException
     */

    private static void skip(BufferedInputStream is, long count) throws IOException {
        while (count > 0) {
            long amt = is.skip(count);
            if (amt == -1) {
                throw new RuntimeException("inputStream skip exception");
            }
            count -= amt;
        }
    }

    /**
     * 读取mp4文件box大小
     *
     * @param is
     * @param buffer
     * @return
     */

    private static int readBoxSize(InputStream is, byte[] buffer) {
        int sz = fill(is, buffer);
        if (sz == -1) {
            return 0;
        }

        return bytesToInt(buffer, 0, 4);

    }

    /**
     * 读取MP4文件box类型
     *
     * @param is
     * @param buffer
     * @return
     */
    private static String readBoxType(InputStream is, byte[] buffer) {
        fill(is, buffer);

        return byteToString(buffer);
    }


    /**
     * byte转换int
     *
     * @param buffer
     * @param pos
     * @param bytes
     * @return
     */
    private static int bytesToInt(byte[] buffer, int pos, int bytes) {
        /*
         * int intvalue = (buffer[pos + 0] & 0xFF) << 24 | (buffer[pos + 1] &
         * 0xFF) << 16 | (buffer[pos + 2] & 0xFF) << 8 | buffer[pos + 3] & 0xFF;
         */
        int retval = 0;
        for (int i = 0; i < bytes; ++i) {
            retval |= (buffer[pos + i] & 0xFF) << (8 * (bytes - i - 1));
        }
        return retval;
    }

    /**
     * byte数据转换String
     *
     * @param buffer
     * @return
     */
    private static String byteToString(byte[] buffer) {
        assert buffer.length == 4;
        String retval = new String();
        try {
            retval = new String(buffer, 0, buffer.length, "ascii");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return retval;
    }

    private static int fill(InputStream stream, byte[] buffer) {
        return fill(stream, 0, buffer.length, buffer);
    }

    /**
     * 读取流数据
     *
     * @param stream
     * @param pos
     * @param len
     * @param buffer
     * @return
     */
    private static int fill(InputStream stream, int pos, int len, byte[] buffer) {
        int readSize = 0;
        try {
            readSize = stream.read(buffer, pos, len);
            if (readSize == -1) {
                return -1;
            }
            assert readSize == len : String.format("len %d readSize %d", len,
                    readSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readSize;
    }
}
