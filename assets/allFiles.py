import glob
import sys

PATH = sys.argv[1]

def get_files_in_dir(dir, fileType):
    return glob.glob(dir+"/*."+fileType)


def main():
    paths = get_files_in_dir(PATH, "png")
    print(paths)

if __name__ == '__main__':
    main()
    