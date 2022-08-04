import PIL.Image
import glob
import sys

ORIGINAL_SIZE = int(sys.argv[2])
RESIZED_SIZE = int(sys.argv[3])
PATH = sys.argv[1]


def read_image(path):
    try:
        image = PIL.Image.open(path)
        return image
    except Exception as e:
        print(e)


def get_files_in_dir(dir, fileType):
    return glob.glob(dir+"/*."+fileType)


def get_image_res(image):
    return image.size


def resize_image(image, height, width):
    resized_image = image.resize((height, width), PIL.Image.NEAREST)
    return resized_image


def main():
    paths = get_files_in_dir(PATH + "/unused", "png")
    for image_path in paths:
        image = read_image(image_path)
        if get_image_res(image)[0] == ORIGINAL_SIZE and get_image_res(image)[1] == ORIGINAL_SIZE:
            image_resized = resize_image(image, RESIZED_SIZE, RESIZED_SIZE)
            print(image_path.replace("unused", "unused/unused_resized"))
            image_resized.save(image_path.replace("unused", "unused/unused_resized"))


if __name__ == '__main__':
    main()
