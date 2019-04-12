
import zipfile
from os.path import dirname, abspath, join
from .mappings import Base
from contextlib import contextmanager

import sqlalchemy

current_dir = dirname(abspath(__file__))

db_copied = False
def ensure_db():
    global db_copied
    if db_copied:
        return

    inpath = join(current_dir, '../../app/src/main/assets/databases/mhgu.db.zip')
    output = join(current_dir, '../temp/')
    f = zipfile.ZipFile(inpath, 'r')
    f.extractall(output)
    f.close()

def read_db():
    ensure_db()
    inpath = abspath(join(current_dir, '../temp/mhgu.db'))
    engine = sqlalchemy.create_engine(f'sqlite:///{inpath}')
    Base.metadata.create_all(engine)

    return sqlalchemy.orm.sessionmaker(bind=engine)()

# adapted from sqlalchemy docs
@contextmanager
def session_scope(sessionmaker):
    """Provide a transactional scope around a series of operations."""
    session = sessionmaker()
    try:
        yield session
        session.commit()
    except:
        session.rollback()
        raise
    finally:
        session.close()
