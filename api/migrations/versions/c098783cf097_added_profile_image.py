"""added profile_image

Revision ID: c098783cf097
Revises: e41f64a7e02b
Create Date: 2024-06-02 23:10:46.034240

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'c098783cf097'
down_revision = 'e41f64a7e02b'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('user', schema=None) as batch_op:
        batch_op.add_column(sa.Column('profile_image', sa.String(length=256), nullable=True))

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('user', schema=None) as batch_op:
        batch_op.drop_column('profile_image')

    # ### end Alembic commands ###