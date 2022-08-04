import PIL.Image
import glob
import sys

PATH = sys.argv[1]
OUT_PATH = sys.argv[2]


def read_image(path):
    try:
        image = PIL.Image.open(path)
        return image
    except Exception as e:
        print(e)


def get_files_in_dir(dir, fileType):
    return glob.glob(dir+"/*/*/*/*."+fileType)


def get_image_res(image):
    return image.size


def resize_image(image, height, width):
    resized_image = image.resize((height, width), PIL.Image.NEAREST)
    return resized_image


def main():
    paths = get_files_in_dir(PATH, "png")
    for image_path in paths:
        print(image_path)
        image = read_image(image_path)
        image.save(OUT_PATH +"/"+ image_path.replace(PATH+"\\", "").replace("\\", "_"))


if __name__ == '__main__':
    main()
