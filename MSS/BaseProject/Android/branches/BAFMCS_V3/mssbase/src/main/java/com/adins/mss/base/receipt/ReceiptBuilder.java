package com.adins.mss.base.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.adins.mss.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loise on 12/04/2018.
 */

/**
 * class untuk membangun struk dalam bentuk gambar
 */
public class ReceiptBuilder {
    List<IDrawItem> listItems = new ArrayList<>();
    private int backgroundColor = Color.WHITE;
    private float textSize;
    private int color = Color.BLACK;
    private int width;
    private int marginTop, marginBottom, marginLeft, marginRight;
    private Typeface typeface;
    private Paint.Align align = Paint.Align.LEFT;

    /**
     * konstruktor untuk inisialisasi lebar struk (biasanya 384)
     * @param width lebar kertas printer dalam pixel
     */
    public ReceiptBuilder(int width) {
        this.width = width;
    }

    /**
     * Mengubah ukuran teks
     * @param textSize ukuran font teks
     * @return
     */
    public ReceiptBuilder setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    /**
     * mengubah warna background struk
     * @param backgroundColor
     * @return
     */
    public ReceiptBuilder setBackgroudColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * mengubah warna teks
     * @param color
     * @return
     */
    public ReceiptBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * mengubah font teks untuk satu baris
     * @param context
     * @param typefacePath
     * @return
     */
    public ReceiptBuilder setTypeface(Context context, String typefacePath) {
        typeface = Typeface.createFromAsset(context.getAssets(), typefacePath);
        return this;
    }

    /**
     * mengembalikan font menjadi default
     * @return
     */
    public ReceiptBuilder setDefaultTypeface() {
        typeface = null;
        return this;
    }

    /**
     * mengubah alignment
     * @param align Paint.Align
     * @return
     */
    public ReceiptBuilder setAlign(Paint.Align align) {
        this.align = align;
        return this;
    }

    /**
     * mengubah margin atas bawah kiri kanan
     * @param margin ukuran margin semua sisi dalam pixel
     * @return
     */
    public ReceiptBuilder setMargin(int margin) {
        this.marginLeft = margin;
        this.marginRight = margin;
        this.marginTop = margin;
        this.marginBottom = margin;
        return this;
    }

    /**
     * mengubah margin atas bawah dan kiri kanan terpisah
     * @param marginTopBottom ukuran margin atas bawah dalam pixel
     * @param marginLeftRight ukuran margin kiri kanan dalam pixel
     * @return
     */
    public ReceiptBuilder setMargin(int marginTopBottom, int marginLeftRight) {
        this.marginLeft = marginLeftRight;
        this.marginRight = marginLeftRight;
        this.marginTop = marginTopBottom;
        this.marginBottom = marginTopBottom;
        return this;
    }


    /**
     * mengubah margin kiri
     * @param margin ukuran margin kiri dalam pixel
     * @return
     */
    public ReceiptBuilder setMarginLeft(int margin) {
        this.marginLeft = margin;
        return this;
    }

    /**
     * mengubah margin kanan
     * @param margin ukuran margin kanan dalam pixel
     * @return
     */
    public ReceiptBuilder setMarginRight(int margin) {
        this.marginRight = margin;
        return this;
    }

    /**
     * mengubah margin atas
     * @param margin ukuran margin atas dalam pixel
     * @return
     */
    public ReceiptBuilder setMarginTop(int margin) {
        this.marginTop = margin;
        return this;
    }

    /**
     * memngubah margin bawah
     * @param margin
     * @return
     */
    public ReceiptBuilder setMarginBottom(int margin) {
        this.marginBottom = margin;
        return this;
    }

    /**
     * menambah objek teks satu baris
     * @param text teks yang ingin digambar
     * @return
     */
    public ReceiptBuilder addText(String text) {
        return addText(text, true);
    }

    /**
     * mengubah objek teks dengan atau tanpa pindah baris
     * @param text teks yang akan digambar
     * @param newLine apakah pindah baris atau tidak
     * @return
     */
    public ReceiptBuilder addText(String text, Boolean newLine) {
        DrawText drawerText = new DrawText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(newLine);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }
        if (align != null) {
            drawerText.setAlign(align);
        }
        listItems.add(drawerText);
        return this;
    }

    /**
     * menambah objek gambar pada struk
     * @param bitmap gambar yang ingin dimasukkan
     * @return
     */
    public ReceiptBuilder addImage(Bitmap bitmap) {
        DrawImage drawerImage = new DrawImage(bitmap);
        if (align != null) {
            drawerImage.setAlign(align);
        }
        listItems.add(drawerImage);
        return this;
    }

    /**
     * menambah objek generik pada struk
     * @param item
     * @return
     */
    public ReceiptBuilder addItem(IDrawItem item) {
        listItems.add(item);
        return this;
    }

    /**
     * menambah area kosong pada struk
     * @param height tinggi area kosong dalam pixel
     * @return
     */
    public ReceiptBuilder addBlankSpace(int height) {
        listItems.add(new DrawBlankSpace(height));
        return this;
    }

    /**
     * menambah paragraf baru dalam teks
     * @return
     */
    public ReceiptBuilder addParagraph() {
        listItems.add(new DrawBlankSpace((int) textSize));
        return this;
    }

    /**
     * menambah garis dari kiri ke kanan kertas
     * @return
     */
    public ReceiptBuilder addLine() {
        return addLine(width - marginRight - marginLeft);
    }

    /**
     * menambah garis dengan lebar tertentu
     * @param size
     * @return
     */
    public ReceiptBuilder addLine(int size) {
        DrawLine line = new DrawLine(size);
        line.setAlign(align);
        line.setColor(color);
        listItems.add(line);
        return this;
    }

    public ReceiptBuilder addLine(int size, int weigth) {
        int _size = size > 0 ? size : (width - marginRight - marginLeft);
        DrawLine line = new DrawLine(_size, weigth);
        line.setAlign(align);
        line.setColor(color);
        listItems.add(line);
        return this;
    }

    /**
     * mengembalikan tinggi struk dalam pixel
     * @return
     */
    private int getHeight() {
        int height = 5 + marginTop + marginBottom;
        for (IDrawItem item : listItems) {
            height += item.getHeight();
        }
        return height;
    }

    /**
     * menggambar semua objek pada canvas
     * @return
     */
    private Bitmap drawImage() {
        Bitmap image = Bitmap.createBitmap(width - marginRight - marginLeft, getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(backgroundColor);
        float size = marginTop;
        for (IDrawItem item : listItems) {
            item.drawOnCanvas(canvas, 0, size);
            size += item.getHeight();
        }
        return image;
    }

    /**
     * mengenerate gambar bitmap dari semua item yang ditambahkan
     * @param context Context
     * @return Bitmap struk
     */
    public Bitmap build(Context context) {
        Bitmap image = Bitmap.createBitmap(width, getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        //Menggambar logo pada samping struk
        //Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.side_logo), null, opt);

//        BitmapDrawable drawable = scaleImage((BitmapDrawable) ContextCompat.getDrawable(context, R.raw.side_logo),(float)1,context);
//        Bitmap temp = drawable.getBitmap();
//        drawable = new BitmapDrawable(context.getResources(),Bitmap.createScaledBitmap(temp,30,164, true));
//        drawable.setTileModeY(Shader.TileMode.REPEAT);
         //comment sampai sini bila tidak mau logo di samping dan sesuaikan margin

        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(drawImage(), marginLeft, 0, paint);

        //lebar area
//        drawable.setBounds(0,0,30,canvas.getHeight());
//        drawable.draw(canvas);
        return image;
    }

    public BitmapDrawable scaleImage (Drawable image, float scaleFactor, Context context) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return (BitmapDrawable)image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(context.getResources(), bitmapResized);
        return (BitmapDrawable)image;

    }

    /**
     * Sample cara membuat struk bitmap, jangan lupa menyediakan file font yang sesuai didalam folder assets
     * @param context
     * @return Bitmap sample struk
     */
    public static Bitmap getTestBitmap(Context context){
        ReceiptBuilder receipt = new ReceiptBuilder(384);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.adins_logo), null, opt);
        receipt.setMarginTop(10).
                setMarginBottom(10).
                setMarginRight(5).
                setMarginLeft(5).
                setAlign(Paint.Align.CENTER).
                addImage(bitmap).
                addParagraph().
                addBlankSpace(20).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(18).
                setTypeface(context, "fonts/RobotoMono-Regular.ttf").
                addText("LakeFront Cafe").
                addText("1234 Main St.").
                addText("Palo Alto, CA 94568").
                addText("999-999-9999").
                addBlankSpace(30).
                setAlign(Paint.Align.LEFT).
                addText("Terminal ID: 123456", false).
                setAlign(Paint.Align.RIGHT).
                addText("1234").
                setAlign(Paint.Align.LEFT).
                addLine().
                addText("08/15/16", false).
                setAlign(Paint.Align.RIGHT).
                addText("SERVER #4").
                setAlign(Paint.Align.LEFT).
                addParagraph().
                addText("CHASE VISA - INSERT").
                addText("AID: A000000000011111").
                addText("ACCT #: *********1111").
                addParagraph().
                setTypeface(context, "fonts/RobotoMono-Bold.ttf").
                addText("CREDIT SALE").
                addText("UID: 12345678", false).
                setAlign(Paint.Align.RIGHT).
                addText("REF #: 1234").
                setTypeface(context, "fonts/RobotoMono-Regular.ttf").
                setAlign(Paint.Align.LEFT).
                addText("BATCH #: 091", false).
                setAlign(Paint.Align.RIGHT).
                addText("AUTH #: 0701C").
                setAlign(Paint.Align.LEFT).
                addParagraph().
                setTypeface(context, "fonts/RobotoMono-Bold.ttf").
                addText("AMOUNT", false).
                setAlign(Paint.Align.RIGHT).
                addText("$ 15.00").
                setAlign(Paint.Align.LEFT).
                addParagraph().
                addText("TIP", false).
                setAlign(Paint.Align.RIGHT).
                addText("$        ").
                addLine(180).
                setAlign(Paint.Align.LEFT).
                addParagraph().
                addText("TOTAL", false).
                setAlign(Paint.Align.RIGHT).
                addText("$        ").
                addLine(180).
                addParagraph().
                setAlign(Paint.Align.CENTER).
                setTypeface(context, "fonts/RobotoMono-Regular.ttf").
                addText("APPROVED");
        receipt.addLine();

        return receipt.build(context);
    }

}
